package app.net.request;

import app.net.auth.Credential;
import app.net.auth.Token;

import java.util.List;

/**
 * <h1>IRequest</h1>
 *
 * @author Dan Ottosson
 */
public interface IRequest {
    void getRequest(Credential credential, Token token, List<String> requestList);
    void postRequest();
}
