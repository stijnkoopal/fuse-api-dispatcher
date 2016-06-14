package al.koop.fuse.api.dispatcher.internal;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static al.koop.fuse.api.dispatcher.internal.DispatchingHelper.DISPATCH_HANDLER_KEY;
import static al.koop.fuse.api.dispatcher.internal.DispatchingHelper.REQUEST_KEY;
import static org.apache.camel.Exchange.HTTP_METHOD;
import static org.apache.camel.Exchange.HTTP_URI;

public class RequestResolver {
    private static final Logger LOG = LoggerFactory.getLogger(RequestResolver.class);

    private final RequestHandlerRegistry requestHandlerRegistry;

    public RequestResolver(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    /**
     * Dispatching takes place in two steps (to support deep auth early): resolve and dispatch.
     * The first step resolves the uri and stores the handler in header DISPATCH_HANDLER_KEY
     *
     * @param clientExchange the incoming exchange
     * @throws Exception
     */
    public void resolve(Exchange clientExchange) throws Exception {
        Message message = clientExchange.getIn();
        HttpServletRequest request = ((HttpMessage) message).getRequest();

        String uri = message.getHeader(HTTP_URI, String.class);
        String method = message.getHeader(HTTP_METHOD, String.class);

        Optional<HandlerWrapper> handlerWrapper = requestHandlerRegistry.findDispatcher(uri, method);
        if (!handlerWrapper.isPresent()) {
            LOG.debug("No mapping found for uri '{}'", uri);
            throw new NotFoundException();
        }

        clientExchange.getIn().setHeader(DISPATCH_HANDLER_KEY, handlerWrapper.get());
        clientExchange.getIn().setHeader(REQUEST_KEY, request);
    }
}
