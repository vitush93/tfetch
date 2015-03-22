package zapoctak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Producer worker class.
 *
 * @author Vít Habada
 */
public class Crawler extends AbstractWorker {

    /**
     * Shared queue reference.
     */
    protected final BlockingQueue<Job> queue;

    /**
     * Job starting page.
     */
    private final int startPage;

    /**
     * Modulus increment.
     */
    private final int increment;

    /**
     * Current page being crawled.
     */
    private int currentPage;

    /**
     * List of collected image URLs.
     */
    private final List<String> collectedLinks;

    /**
     * Stream reader.
     */
    private BufferedReader stream;
    
    /**
     * Invoke crawling stop.
     */
    private boolean stop;

    /**
     *
     * @param q
     * @param inc
     * @param start
     */
    public Crawler(BlockingQueue<Job> q, int start, int inc) {
        queue = q;
        increment = inc;
        startPage = start;
        currentPage = startPage;
        collectedLinks = new ArrayList<>();
    }

    /**
     * Updates the current stream by a new URL.
     *
     * @param page
     * @throws IOException
     * @throws MalformedURLException
     * @throws InvalidOperationException
     */
    private void updateStream() throws IOException, MalformedURLException, InvalidOperationException {
        URL website = new URL("http://" + Job.getUrl() + ".tumblr.com/page/" + currentPage);
        URLConnection conn = website.openConnection();
        stream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    /**
     * Read the URL contents to a String.
     *
     * @return
     * @throws InvalidOperationException
     * @throws IOException
     */
    private String readHTML() throws InvalidOperationException, IOException {
        if (stream == null) {
            throw new InvalidOperationException("Stream has not been set.");
        }

        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = stream.readLine()) != null) {
            sb.append(s);
        }
        
        stream.close();

        return sb.toString();
    }

    /**
     * Constructs a Job object containing list of image URLs from a HTML data.
     *
     * @return
     * @throws IOException
     * @throws InvalidOperationException
     * @throws InvalidArgumentException
     */
    private void crawlCurrentPage() throws IOException, InvalidOperationException, InvalidArgumentException {
        String data = readHTML();

        Pattern pattern = Pattern.compile("<\\s*img\\s+.*?src\\s*\\s*=?[\",',=](.*?)[\",',\\ ,>].*?>?");
        Matcher matcher = pattern.matcher(data);
        Pattern p = Pattern.compile("http:\\/\\/[0-9]+\\.media\\S*?");

        while (matcher.find()) {
            String f = matcher.group(1);
            Matcher m = p.matcher(f);
            if (m.find()) {
                // TODO: various sizes collectedLinks.add(f.replace("_500", "_1280"));
                collectedLinks.add(f);
            }
        }
    }

    /**
     * Returns list of links and instantiate a new empty list for collection.
     *
     * @return List of crawled links so far.
     */
    private List<String> flushCollected() {
        List<String> coll = new ArrayList<>();
        collectedLinks.stream().forEach((s) -> {
            coll.add(s);
        });
        collectedLinks.clear();

        return coll;
    }

    /**
     *
     * @return Crawler's starting page
     */
    public int getStartPage() {
        return startPage;
    }
    
    /**
     * Close any unfinished stream.
     * 
     * @throws IOException 
     */
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void run() {
        try {
            for (;;) {
                if (cancelRequested || stop) {
                    break;
                }
                crawl();
            }
        } catch (IOException | InvalidOperationException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crawl() throws IOException, InterruptedException, InvalidArgumentException, InvalidOperationException {
        System.out.println("queue size: " + queue.size());

        // wait if queue is full
        while (queue.size() == TumblrFetchingService.QUEUE_SIZE) {
            synchronized (queue) {
                queue.wait();
            }
        }

        // otherwise continue crawling and generating jobs
        int j = 0;
        for (int i = startPage; i <= startPage + increment * Job.PAGES_PER_JOB; i = i + increment) {
            System.out.println("current page: " + currentPage);

            if (j++ == Job.PAGES_PER_JOB - 1) {
                List<String> collected = flushCollected();
                
                if (collected.isEmpty()) {
                    stop = true;
                    break;
                }

                Job job = new Job(collected);
                synchronized (queue) {
                    queue.put(job);
                    queue.notify();
                }
                j = 0;
            }
            
            currentPage += increment;
            updateStream(); // load next page
            crawlCurrentPage(); // crawl
        }
    }
}
