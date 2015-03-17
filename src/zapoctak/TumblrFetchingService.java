package zapoctak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TumblrFetchingService {

    private final BlockingQueue<Job> sharedQueue;

    private final List<Runnable> producers;

    private final List<Runnable> consumers;

    public TumblrFetchingService(String blog) throws InvalidArgumentException {
        Job.setUrl(blog);

        sharedQueue = new ArrayBlockingQueue<>(10);
        producers = new ArrayList<>();
        consumers = new ArrayList<>();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            producers.add(new Crawler(blog, sharedQueue));
        }
    }

    public void execute() {
        try {
            URL website = new URL("http://cutegirls33.tumblr.com/");
            URLConnection conn = website.openConnection();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String s;
            while((s = bfr.readLine()) != null) System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
