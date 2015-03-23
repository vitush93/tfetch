
package zapoctak;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;

/**
 * Consumer worker class.
 * 
 * @author Vít
 */
public class Fetcher extends AbstractWorker {

    /**
     * Shared queue reference.
     */
    private final BlockingQueue<Job> queue;

    /**
     * 
     * @param q 
     */
    public Fetcher(BlockingQueue<Job> q) {
        queue = q;
    }

    @Override
    public void run() {
        while (true) {
            if (cancelRequested || (queue.isEmpty() && Crawler.deadCount == TumblrFetchingService.CRAWLER_COUNT)) {
                break;
            }

            try {
                fetch();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Polls single Job from the Queue and process it.
     * 
     * @throws InterruptedException
     * @throws IOException 
     */
    private void fetch() throws InterruptedException, IOException {
        while (queue.isEmpty() && !cancelRequested) {
            synchronized (queue) {
                queue.wait();
            }
        }

        synchronized (queue) {
            queue.notifyAll();
        }

        Job job = queue.poll();
        if (job == null) {
            return;
        }

        for (String url : job.getImages()) {
            if (cancelRequested) {
                break;
            }
            saveImage(url);
        }
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
