package app.net.auth;
/**
 * <h1>AbstractConnector</h1>
 * Abstract connector class.
 * Hold user credentials and token if received
 *
 * @author Dan Ottosson
 */
public abstract class AbstractConnector {
    protected Token token;
    protected Credential credential;
    public AbstractConnector() {}

    /**
     * Method to connect
     */
    public void connect() {}

    /**
     * Method to get the token as a string
     * @return token as string
     */
    public String tokenAsString() {
        return token.toString();
    }
}
