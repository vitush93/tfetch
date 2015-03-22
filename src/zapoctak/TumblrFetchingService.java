package zapoctak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core fetching functionality.
 *
 * @author Vít Habada
 */
public class TumblrFetchingService {

    public static final int QUEUE_SIZE = 20;

    /**
     * Shared queue for the consumer-producer pattern.
     */
    private final BlockingQueue<Job> sharedQueue;

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
        Job.setUrl(blog);

        sharedQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
    }

    /**
     * Stop the fetching process and all workers.
     */
    public void stop() {
        Crawler.cancelRequested = true;
        // TODO: join all threads
    }

    /**
     * Start the fetching process.
     */
    public void start() {
        int threadCount = Runtime.getRuntime().availableProcessors();

        // init producers - split by equivalence classes
        producers.add(new Thread(new Crawler(sharedQueue, threadCount, threadCount)));
        for (int i = 1; i < threadCount; i++) {
            producers.add(new Thread(new Crawler(sharedQueue, i % threadCount, threadCount)));
        }

        // init consumers
        for (int i = 0; i < threadCount * 4; i++) {
            consumers.add(new Thread(new Fetcher(sharedQueue)));
        }

        // reset cancel flag
        Crawler.cancelRequested = false;

        // start all producers
        producers.stream().forEach((t) -> {
            t.start();
        });

        // start all consumers
        consumers.stream().forEach((t) -> {
            t.start();
        });
    }

}
