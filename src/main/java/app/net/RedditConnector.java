package app.net;

import app.net.auth.AbstractConnector;
import app.net.auth.Credential;
import app.net.auth.IToken;
import app.net.auth.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <h1>RedditConnector</h1>
 * @author Dan Ottosson
 */
public class RedditConnector extends AbstractConnector {
    private final static String PROPERTIES = "src/main/resources/application.properties";
    private final static String ACCESS_TOKEN_URI =
            "https://www.reddit.com/api/v1/access_token";

    public RedditConnector() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(PROPERTIES));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        this.credential = new Credential(
                properties.getProperty("reddit-agent"),
                properties.getProperty("reddit-username"),
                properties.getProperty("reddit-password"),
                properties.getProperty("reddit-clientID"),
                properties.getProperty("reddit-clientSecret")
        );
    }

    public RedditConnector(Credential credential) {
        this.credential = credential;
    }

    public RedditConnector(String agent, String username, String password,
                           String clientID, String clientSecret) {
        this.credential =
                new Credential(agent, username, password, clientID, clientSecret);
    }
    @Override
    public void connect() {
        this.setAccessToken();
    }

    private void setAccessToken() {
        IToken iToken = () -> {
            HttpRequest request;
            HttpResponse<String> response;

            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(ACCESS_TOKEN_URI))
                        .headers("content-type", "application/x-www-form-urlencoded", "User-Agent", credential.agent())
                        .POST(HttpRequest.BodyPublishers.ofString(
                                String.format(
                                        "grant_type=password&username=%s&password=%s",
                                        credential.username(),
                                        credential.password()
                                )
                        ))
                        .build();

            } catch (URISyntaxException e) {
                System.err.println("URI do not match");
                System.err.println(e.getMessage());
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            Gson gson = new GsonBuilder().create();

            try {
                response = HttpClient.newBuilder()
                        .authenticator(new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(credential.clientID(),
                                        credential.clientSecret().toCharArray());
                            }
                        })
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return null;
            }

            if (response != null) {
                if (response.statusCode() == 200) {
                    try {
                        Token t = mapper.readValue(response.body(), Token.class);
                        return t;

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }

                } else System.err.println(response.statusCode());
            }
            return null;
        };

        this.token = iToken.requestAccessToken();
    }
}
