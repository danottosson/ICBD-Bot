package app.net.auth;

/**
 * <h1>IToken</h1>
 * Functional interface to request an access token
 *
 * @author Dan Ottosson
 */
@FunctionalInterface
public interface IToken {
    Token requestAccessToken();
}
