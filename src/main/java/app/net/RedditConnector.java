package app.net;

import app.net.auth.AbstractConnector;
import app.net.auth.IToken;
import app.net.auth.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * <h1>RedditConnector</h1>
 * @author Dan Ottosson
 */
public class RedditConnector extends AbstractConnector {
    public RedditConnector() {

    }

    private void setAccessToken() {
        IToken iToken = () -> {
            HttpRequest request;
            HttpResponse<String> response;

            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(""))
                        .headers("", "", "", credential.agent())
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
                        return mapper.readValue(response.body(), Token[].class)[0];

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
