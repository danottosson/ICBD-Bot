package app.net.auth;

/**
 * <h1>Token</h1>
 *
 * @author Dan Ottosson
 */
public record Token(String access_token, String token_type, long expires_in,
                    String scope) {
    /**
     * String representation of a Token object.
     * @return Token as string
     */
    public String toString() {
        return String.format(
                "Token: %s\nType: %s\nExpire: %d\nScope: %s\n",
                access_token,
                token_type,
                expires_in,
                scope
        );
    }
}
