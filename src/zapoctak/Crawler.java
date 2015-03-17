package zapoctak;

import java.util.concurrent.BlockingQueue;

public class Crawler implements Runnable {
    
    private BlockingQueue<Job> queue;
    
    public Crawler(String blog, BlockingQueue<Job> q) {
        queue = q;
    }
    
    @Override
    public void run() {
        
    }
}
