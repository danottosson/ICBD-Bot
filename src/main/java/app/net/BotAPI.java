package app.net;

import app.net.auth.AbstractConnector;
import app.net.auth.Credential;

/**
 * <h1>RedditRequest</h1>
 * @author Dan Ottosson
 */
public class BotAPI implements Runnable {
    private static AbstractConnector connector = null;

    public BotAPI(String ...args) {
        if (args.length == 5) {
            connector = new RedditConnector(args[0],args[1],args[2],args[3],args[4]);
        }
        else {
            connector = new RedditConnector();
        }
    }

    public BotAPI(Credential credential) {
        connector = new RedditConnector(credential);
    }

    private void connect() {
        connector.connect();
    }

    public static void main(String[] args) {
        Thread t = new Thread(new BotAPI(
                "ICBD-Bot/0.1 by Turbulent_Professor8",
                "Turbulent_Professor8",
                "bJpwJwnP82_xLh6",
                "QivFSZerdfZuSW8Mj17Tww",
                "emtHSVlrSYQjJf0BljKRQodWoRNd0w"){});
        t.start();
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        // Fetch access token
        connect();

        System.out.println("End of run");
    }
}
