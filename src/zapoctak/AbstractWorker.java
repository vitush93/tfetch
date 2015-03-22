package zapoctak;

public abstract class AbstractWorker implements Runnable {
    
    /**
     * Global cancel request flag.
     */
    public static boolean cancelRequested;
}
