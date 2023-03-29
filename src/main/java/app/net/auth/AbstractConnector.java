package app.net.auth;
/**
 * <h1>AbstractConnector</h1>
 * @author Dan Ottosson
 */
public abstract class AbstractConnector {
    protected Token token;
    protected Credential credential;
    public AbstractConnector() {}
    public void connect() {}
    public String tokenAsString() {
        return token.toString();
    }
}
