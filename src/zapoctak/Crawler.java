
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
import java.util.concurrent.atomic.AtomicInteger;
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
     * Number of finished crawlers.
     */
    public static AtomicInteger deadCount = new AtomicInteger(0);

    /**
     * Shared queue reference.
     */
    private final BlockingQueue<Job> queue;

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
     * Crawling has finished.
     */
    private boolean finished;

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
        URL website = new URL(Job.getUrl() + "page/" + currentPage);
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
                if (cancelRequested || finished) {
                    deadCount.incrementAndGet();
                    break;
                }
                crawl();
            }
        } catch (IOException | InvalidOperationException | InvalidArgumentException e) {
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Crawls page by page and produces Job instances on the way.
     * 
     * @throws IOException
     * @throws InterruptedException
     * @throws InvalidArgumentException
     * @throws InvalidOperationException 
     */
    private void crawl() throws IOException, InterruptedException, InvalidArgumentException, InvalidOperationException {
        
        // wait if queue is full
        while (queue.size() == TumblrFetchingService.QUEUE_SIZE && !cancelRequested) {
            synchronized (queue) {
                queue.wait();
            }
        }

        // otherwise continue crawling and generating jobs
        int j = 0;
        for (int i = startPage; i <= startPage + increment * Job.PAGES_PER_JOB; i = i + increment) {
            if (j++ == Job.PAGES_PER_JOB - 1) {
                List<String> collected = flushCollected();

                if (collected.isEmpty()) {
                    finished = true;
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
