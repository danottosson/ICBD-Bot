package app.net.auth;

/**
 * <h1>Credential</h1>
 *
 * @author Dan Ottosson
 */
public record Credential(String agent, String username, String password,
                         String clientID, String clientSecret) {}
