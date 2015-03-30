package zapoctak;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer worker class.
 *
 * @author Vít
 */
public class Fetcher extends AbstractWorker {

    /**
     * Number of finished fetchers.
     */
    public static AtomicInteger deadCount = new AtomicInteger(0);
    
    /**
     * Shared queue reference.
     */
    private final BlockingQueue<String> queue;

    /**
     *
     * @param q
     */
    public Fetcher(BlockingQueue<String> q) {
        queue = q;
    }

    /**
     * General termination conditions.
     *
     * @return
     */
    private boolean finishedCondition() {
        return (queue.isEmpty() && Crawler.deadCount.get() == TumblrFetchingService.CRAWLER_COUNT);
    }

    @Override
    public void run() {
        while (true) {
            if (cancelRequested || finishedCondition()) {
                deadCount.incrementAndGet();
                break;
            }

            try {
                fetch();
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {

            }
        }

        // notify other fetchers that the job has finished
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    /**
     * Polls single Job from the Queue and process it.
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private void fetch() throws InterruptedException, IOException {
        
        // wait if Q is empty
        boolean crawlersStillAlive = Crawler.deadCount.get() != TumblrFetchingService.CRAWLER_COUNT; 
        while (queue.isEmpty() && crawlersStillAlive && !cancelRequested) {
            synchronized (queue) {
                queue.wait();
            }
        }

        // process polled item
        String url = queue.poll();
        if (url == null) {
            return;
        }

        // inform other threads about polling
        synchronized (queue) {
            queue.notify();
        }

        // download image
        saveImage(url);
    }

    /**
     * Download an image from URL.
     *
     * @param url
     * @throws IOException
     */
    private void saveImage(String url) throws IOException {
        URL website = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) website.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        if (conn.getResponseCode() != 200) {
            return;
        }

        ReadableByteChannel rbc = Channels.newChannel(website.openStream());

        try (FileOutputStream fos = new FileOutputStream(url.substring(url.lastIndexOf('/') + 1, url.length()))) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

}
