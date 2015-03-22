package zapoctak;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;

public class Fetcher extends AbstractWorker {

    /**
     * Shared queue reference.
     */
    protected final BlockingQueue<Job> queue;

    public Fetcher(BlockingQueue<Job> q) {
        queue = q;
    }

    @Override
    public void run() {
        while (true) {
            if (cancelRequested) {
                break;
            }

            try {
                fetch();
                Thread.sleep(50);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fetch() throws InterruptedException, IOException {
        while (queue.isEmpty()) {
            synchronized (queue) {
                queue.wait();
            }
        }

        synchronized (queue) {
            queue.notifyAll();
        }

        System.out.println("processing job..");
        Job job = queue.poll();
        if(job == null) return;
        for (String url : job.getImages()) {
            if (cancelRequested) {
                break;
            }
            saveImage(url);
        }
    }

    private void saveImage(String url) throws IOException {
        URL website = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) website.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        if (conn.getResponseCode() != 200) {
            System.out.println("error : " + conn.getResponseCode() + " " + url);
            return;
        }

        System.out.println("fetching " + url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());

        FileOutputStream fos = new FileOutputStream(url.substring(url.lastIndexOf('/') + 1, url.length()));
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        fos.close();
    }

}
