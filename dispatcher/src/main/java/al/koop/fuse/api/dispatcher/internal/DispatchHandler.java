package al.koop.fuse.api.dispatcher.internal;

import al.koop.fuse.api.dispatcher.RequestHandler;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.http.HttpMessage;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static org.apache.camel.Exchange.HTTP_METHOD;
import static org.apache.camel.Exchange.HTTP_URI;

public class DispatchHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DispatchHandler.class);

    public static final String DISPATCH_HANDLER_HEADER = "API-Dispatcher";
    public static final String REQUEST = "API-Request";

    private final RequestHandlerRegistry requestHandlerRegistry;

    public DispatchHandler(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    /**
     * Dispatching takes place in two steps (to support deep auth early): resolve and dispatch.
     * The first step resolves the uri and stores the handler in header DISPATCH_HANDLER_HEADER
     *
     * @param clientExchange the incoming exchange
     * @throws Exception
     */
    public void resolve(Exchange clientExchange) throws Exception {
        Message message = clientExchange.getIn();
        HttpServletRequest request = ((HttpMessage) message).getRequest();

        String uri = message.getHeader(HTTP_URI, String.class);
        String method = message.getHeader(HTTP_METHOD, String.class);

        Optional<HandlerWrapper> handlerAndRoles = requestHandlerRegistry.findDispatcher(uri, method);
        if (!handlerAndRoles.isPresent()) {
            LOG.debug("No mapping found for uri '{}'", uri);
            throw new NotFoundException();
        }

        clientExchange.getIn().setHeader(DISPATCH_HANDLER_HEADER, handlerAndRoles.get());
        clientExchange.getIn().setHeader(REQUEST, request);
    }

    /**
     * Dispatching takes place in two steps (to force deep auth early): resolve and dispatch.
     * The second step looks up the handler in header DISPATCH_HANDLER_HEADER and performs
     * actual dispatch.
     *
     * @param clientExchange the incoming exchange
     * @throws Exception
     */
    public void dispatch(Exchange clientExchange) throws Exception {
        Message message = clientExchange.getIn();

        Object dispatchRequestHandlerObject = message.removeHeader(DISPATCH_HANDLER_HEADER);

        if (!(dispatchRequestHandlerObject instanceof HandlerWrapper)) {
            LOG.debug("No dispatcher found");
            throw new IllegalStateException("No dispatcher found");
        }

        HandlerWrapper handlerWrapper = (HandlerWrapper) dispatchRequestHandlerObject;
        RequestHandler dispatchRequestHandler = handlerWrapper.getRequestHandler();

        LOG.debug("Dispatching to  '{}'", dispatchRequestHandler.getCamelContext().toString());
        Exchange serviceExchange = getDispatcherExchange(clientExchange, dispatchRequestHandler);
        dispatchRequestHandler.handle(serviceExchange);
        copyResponse(clientExchange, serviceExchange);
    }

    /**
     * Copy over exchanges
     *
     * @param clientExchange exchange to copy from
     * @param serviceExchange exchange to copy to
     */
    private void copyResponse(Exchange clientExchange, Exchange serviceExchange) {
        clientExchange.setException(serviceExchange.getException());
        clientExchange.setIn(serviceExchange.getIn());
        clientExchange.setOut(serviceExchange.getOut());
    }

    private Exchange getDispatcherExchange(Exchange clientExchange, RequestHandler dispatchRequestHandler) {
        final DefaultExchange serviceExchange = new DefaultExchange(dispatchRequestHandler.getCamelContext());
        serviceExchange.setPattern(clientExchange.getPattern());
        serviceExchange.setFromEndpoint(clientExchange.getFromEndpoint());
        serviceExchange.setFromRouteId(clientExchange.getFromRouteId());
        serviceExchange.setIn(clientExchange.getIn());

        serviceExchange.getProperties().putAll(clientExchange.getProperties());

        return serviceExchange;
    }
}
