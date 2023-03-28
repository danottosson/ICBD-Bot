package app.net.auth;

/**
 * <h1>Token</h1>
 *
 * @author Dan Ottosson
 */
public record Token(String token, String type, int expire, String scope) {
}
