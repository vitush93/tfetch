package zapoctak;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable {

    private final BlockingQueue<Job> queue;

    public Parser(BlockingQueue<Job> q) {
        queue = q;
    }

    @Override
    public void run() {
        // sample code
        try {
            URL website = new URL("http://36.media.tumblr.com/d4b9d1508973efd0b1cfb49c4363333b/tumblr_n0dv3vlZYh1smzycho1_500.jpg");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());

            FileOutputStream fos = new FileOutputStream("image.jpg");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception ex) {

        }
    }

}
