package zapoctak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.util.concurrent.BlockingQueue;

public class Crawler implements Runnable {

    /**
     * Number of blog pages per working thread.
     */
    private static final int PAGES_PER_JOB = 5;

    /**
     * Shared queue reference.
     */
    private final BlockingQueue<Job> queue;

    /**
     * Job starting page.
     */
    private int startPage;

    /**
     *
     * @param blog
     * @param q
     */
    public Crawler(String blog, BlockingQueue<Job> q) {
        queue = q;
    }

    /**
     *
     * @return Crawler's starting page
     */
    public int getStartPage() {
        return startPage;
    }

    @Override
    public void run() {
    }
}
