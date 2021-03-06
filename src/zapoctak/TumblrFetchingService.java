package zapoctak;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Core fetching functionality.
 *
 * @author Vít Habada
 */
public class TumblrFetchingService {

    public static final int QUEUE_SIZE = 20;

    // Worker counts must be a prime number
    public static final int CRAWLER_COUNT = 3;
    public static final int FETCHER_COUNT = 17;
    
    // Working URL.
    public static String URL;

    /**
     * Shared queue for the consumer-producer pattern.
     */
    private final BlockingQueue<String> sharedQueue;
    
    /**
     * Set of downloaded images hash codes.
     */
    private static final HashSet<Integer> imageSet = new HashSet<>();

    /**
     * List of producers.
     */
    private final List<Thread> producers;

    /**
     * List of consumers.
     */
    private final List<Thread> consumers;

    /**
     *
     * @param blog
     * @throws InvalidArgumentException
     */
    public TumblrFetchingService(String blog) throws InvalidArgumentException {
        if (!blog.endsWith("/")) {
            blog = blog + "/";
        }
        URL = blog;

        sharedQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
    }

    /**
     * Stop the fetching process and all workers.
     *
     * @throws java.lang.InterruptedException
     */
    public void stop() throws InterruptedException {
        Crawler.cancelRequested = true;

        synchronized (sharedQueue) {
            sharedQueue.notifyAll();
        }

        for (Thread t : producers) {
            t.join();
        }

        for (Thread t : consumers) {
            t.join();
        }
    }

    /**
     * Start the fetching process.
     *
     * @throws zapoctak.InvalidArgumentException
     */
    public void start() throws InvalidArgumentException {

        // init producers - split by equivalence classes
        Thread f = new Thread(new Crawler(sharedQueue, CRAWLER_COUNT, CRAWLER_COUNT));
        f.setName("Crawler " + CRAWLER_COUNT);
        producers.add(f);
        for (int i = 1; i < CRAWLER_COUNT; i++) {
            Thread t = new Thread(new Crawler(sharedQueue, i % CRAWLER_COUNT, CRAWLER_COUNT));
            t.setName("Crawler " + i);

            producers.add(t);
        }

        // init consumers
        for (int i = 0; i < FETCHER_COUNT; i++) {
            Thread t = new Thread(new Fetcher(sharedQueue));
            t.setName("Fetcher " + (i + 1));

            consumers.add(t);
        }
        
        // reset image hashset
        imageSet.clear();

        // reset cancel flag
        Crawler.cancelRequested = false;

        // reset deadCount
        Crawler.deadCount.set(0);
        Fetcher.deadCount.set(0);

        // start all producers
        producers.stream().forEach((t) -> {
            t.start();
        });

        // start all consumers
        consumers.stream().forEach((t) -> {
            t.start();
        });
    }
    
    /**
     * Adds the passed string's hashcode to the set.
     * 
     * @param s Image URL
     * @return true if adding to the set was successful
     */
    public static synchronized boolean addImageHash(String s) {
        return imageSet.add(s.hashCode());
    }

}
