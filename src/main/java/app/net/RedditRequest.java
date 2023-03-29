package app.net;

import app.net.auth.AbstractConnector;
import app.net.auth.Credential;

/**
 * <h1>RedditRequest</h1>
 * @author Dan Ottosson
 */
public class RedditRequest {
    private static AbstractConnector connector = null;

    public RedditRequest(String ...args) {
        if (args.length == 5) {
            connector = new RedditConnector(args[0],args[1],args[2],args[3],args[4]);
        }
        else {
            connector = new RedditConnector();
        }
    }

    public RedditRequest(Credential credential) {
        connector = new RedditConnector(credential);
    }

    public void connect() {
        connector.connect();
    }

    public static void main(String[] args) {
        RedditRequest redditRequest = new RedditRequest("ICBD-Bot/0.1 by " +
                "Turbulent_Professor8","Turbulent_Professor8","bJpwJwnP82_xLh6",
                "QivFSZerdfZuSW8Mj17Tww","emtHSVlrSYQjJf0BljKRQodWoRNd0w");

        redditRequest.connect();
        System.out.println(connector.tokenAsString());
    }
}
