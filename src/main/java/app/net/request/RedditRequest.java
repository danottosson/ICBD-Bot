package app.net.request;

import app.net.auth.Credential;
import app.net.auth.Token;
import app.net.request.thing.Child;
import app.net.request.thing.Data;
import app.net.request.thing.Listing;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * <h1>RedditRequest</h1>
 * @author Dan Ottosson
 */
public class RedditRequest extends AbstractRequest {
    private static final String REQUEST_URI = "https://oauth.reddit.com/r/";
    private final Gson gson = new Gson();

    private static String getSubredditRequestUri(String subreddit) {
        return String.format(REQUEST_URI+"%s", subreddit);
    }
    private static String getSubredditCommentsRequestUri(String subreddit, String article) {
        return String.format(REQUEST_URI + "%s/comments/%s", subreddit, article);
    }

    private static URI getUriFromString(String uri) throws URISyntaxException {
        return new URI(uri);
    }

    public HttpResponse<String> getHttpResponseFromGetRequest(HttpRequest request) {
        try {
            return HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getRequest(Credential credential, Token token, List<String> requestList) {
        HttpRequest request = null;
        HttpResponse<String> response;

        for (String subreddit : requestList) {
            try {
                request = HttpRequest.newBuilder()
                        .uri(getUriFromString(
                                getSubredditRequestUri(
                                        subreddit
                                )
                        ))
                        .headers("Authorization",
                                token.token_type() + " " + token.access_token(),
                                "User-Agent",
                                credential.agent()
                        )
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

            System.out.println(redditRequestLimitHeadersToString(response));

            Listing object = gson.fromJson(response.body(), Listing.class);

            List<String> links = new ArrayList<>();
            for (Child c : object.data().children()) {
                links.add(c.data().name());
            }

            HttpRequest request1 = null;
            HttpResponse<String> response1;

            System.out.println(links);
            try {
                request1 = HttpRequest.newBuilder()
                        .uri(getUriFromString(
                                getSubredditCommentsRequestUri(
                                        subreddit,
                                        links.get(1).replaceAll("t3_","")
                                )
                        ))
                        .headers("Authorization",
                                token.token_type() + " " + token.access_token(),
                                "User-Agent",
                                credential.agent()
                        )
                        .GET()
                        .build();

            } catch (URISyntaxException e) {
                System.err.println("URI do not match");
                System.err.println(e.getMessage());
            }

            try {
                response1 = HttpClient.newBuilder()
                        .build()
                        .send(request1, HttpResponse.BodyHandlers.ofString());

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(redditRequestLimitHeadersToString(response1));

            System.out.println(response1.body());

            Listing[] response1Listing = gson.fromJson(response1.body(), Listing[].class);

            assert response1Listing != null;
            List<Child> children = Arrays.stream(response1Listing)
                    .map(Listing::data)
                    .map(Data::children)
                    .flatMap(Collection::stream)
                    .filter(child -> child.kind().equals("t1"))
                    .toList();

            children.forEach(child -> System.out.println("\n" + child.data().name() + "\n" + child.data().body()));
        }

    }

    @Override
    public void postRequest() {

    }

    public String redditRequestLimitHeadersToString(HttpResponse<String> response) {
        Map<String,List<String>> headersMap = response.headers().map();
        return "Limit remaining: " + headersMap.get(LIMIT_REMAINING) + "\n" +
                "Limit reset: " + headersMap.get(LIMIT_RESET) + "\n" +
                "Limit used: " + headersMap.get(LIMIT_USED);
    }
}
