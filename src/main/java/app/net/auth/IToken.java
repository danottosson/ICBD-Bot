package app.net.auth;

/**
 * <h1>IToken</h1>
 *
 * @author Dan Ottosson
 */
@FunctionalInterface
public interface IToken {
    Token requestAccessToken();
}
