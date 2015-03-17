package zapoctak;

import java.util.ArrayList;
import java.util.List;

public class Job {

    /**
     * Working URL.
     */
    private static String URL;

    /**
     * List of collected links.
     */
    private List<String> images;

    /**
     * Flag to indicate whether the job has been completed.
     */
    private boolean done;

    /**
     * @param page Job starting page
     * @throws InvalidArgumentException
     */
    public Job() throws InvalidArgumentException {
        if (URL.length() == 0) {
            throw new InvalidArgumentException("Working URL is not set.");
        }
        
        images = new ArrayList<>();
    }

    /**
     *
     * @param s Blog URL
     * @throws InvalidArgumentException
     */
    public static void setUrl(String s) throws InvalidArgumentException {
        if (s.length() == 0) {
            throw new InvalidArgumentException("Working URL cannot be empty.");
        }

        URL = s;
    }

    /**
     * Adds a new image to the job.
     *
     * @param img Image URL
     */
    public void addImage(String img) {
        images.add(img);
    }

    /**
     *
     * @return Complete list of images
     * @throws InvalidOperationException
     */
    public List<String> getImages() throws InvalidOperationException {
        if (!done) {
            throw new InvalidOperationException("Job has not been processed yet.");
        }

        return images;
    }
}