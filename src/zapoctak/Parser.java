package zapoctak;

import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable {
    
    private BlockingQueue<String> queue;
    
    public Parser(BlockingQueue<String> q) {
        queue = q;
    }
    
    @Override
    public void run() {
        
    }
        
}
