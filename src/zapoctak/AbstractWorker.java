
package zapoctak;

/**
 * Abstract Worker.
 * 
 * @author Vít Habada
 */
public abstract class AbstractWorker implements Runnable {
    
    /**
     * Global cancel request flag.
     */
    public static boolean cancelRequested;
}
