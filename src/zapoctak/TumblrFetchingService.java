package zapoctak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TumblrFetchingService {

    private BlockingQueue<String> sharedQueue;

    private List<Runnable> producers;

    private List<Runnable> consumers;

    public TumblrFetchingService(String blog) {
        sharedQueue = new ArrayBlockingQueue<>(10);
        producers = new ArrayList<>();
        consumers = new ArrayList<>();

        int threadCount = Runtime.getRuntime().availableProcessors(); // TODO
    }

    public void execute() {

    }

}
