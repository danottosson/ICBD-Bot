package app.net.request;

import app.net.auth.Credential;
import app.net.auth.Token;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * <h1>RedditRequest</h1>
 * @author Dan Ottosson
 */
public class RedditRequest extends AbstractRequest {
    private static final String REQUEST_URI = "https://oauth.reddit.com/r/";
    @Override
    public void getRequest(Credential credential, Token token, List<String> requestList) {
        HttpRequest request = null;
        HttpResponse<String> response;

        for (String s: requestList) {
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(REQUEST_URI + s))
                        .headers("Authorization",
                                token.token_type() + " " + token.access_token(), "User" +
                                        "-Agent", credential.agent())
                        .GET()
                        .build();

            } catch (URISyntaxException e) {
                System.err.println("URI do not match");
                System.err.println(e.getMessage());
            }

            try {
                response = HttpClient.newBuilder()
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void postRequest() {

    }
}
