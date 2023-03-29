package app.net.request;
/**
 * <h1>AbstractRequest</h1>
 * @author Dan Ottosson
 */
public abstract class AbstractRequest implements IRequest {
    protected static final String LIMIT_USED = "X-Ratelimit-Used";
    protected static final String LIMIT_REMAINING = "X-Ratelimit-Remaining";
    protected static final String LIMIT_RESET = "X-Ratelimit-Reset";
    public AbstractRequest() {}
}
