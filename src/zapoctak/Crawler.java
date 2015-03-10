package zapoctak;

import java.util.concurrent.BlockingQueue;

public class Crawler implements Runnable {
    
    private BlockingQueue<String> queue;
    
    public Crawler(String blog, BlockingQueue<String> q) {
        queue = q;
    }
    
    @Override
    public void run() {
        for(int i = 0; i < 100; i++) {
            queue.add(new String());
        }
    }
}
