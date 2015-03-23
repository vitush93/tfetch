
package zapoctak;

import java.util.List;

/**
 * Consumer's Job container.
 *
 * @author Vít Habada
 */
public class Job {

    /**
     * Number of blog pages per working thread.
     */
    public static final int PAGES_PER_JOB = 5;

    /**
     * End of blog flag.
     */
    public static boolean reachedEnd;

    /**
     * Working blog URL.
     */
    private static String URL;

    /**
     * List of collected links.
     */
    private List<String> images;

    /**
     * @param l
     * @throws InvalidArgumentException
     */
    public Job(List<String> l) throws InvalidArgumentException {
        if (URL.length() == 0) {
            throw new InvalidArgumentException("Working URL is not set.");
        }

        images = l;
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

        if (!s.endsWith("/")) {
            s = s + "/";
        }

        URL = s;
    }

    /**
     *
     * @return @throws InvalidOperationException
     */
    public static String getUrl() throws InvalidOperationException {
        if (URL.length() == 0) {
            throw new InvalidOperationException("Working URL is not set.");
        }

        return URL;
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
     */
    public List<String> getImages() {
        return images;
    }
}
