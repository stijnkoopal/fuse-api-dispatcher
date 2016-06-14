package al.koop.fuse.api.dispatcher.internal;

import al.koop.fuse.api.dispatcher.RequestHandler;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static al.koop.fuse.api.dispatcher.internal.DispatchingHelper.DISPATCH_HANDLER_KEY;

public class RequestDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);

    /**
     * Dispatching takes place in two steps (to force deep auth early): resolve and dispatch.
     * The second step looks up the handler in header DISPATCH_HANDLER_KEY and performs
     * actual dispatch.
     *
     * @param clientExchange the incoming exchange
     * @throws Exception
     */
    public void dispatch(Exchange clientExchange) throws Exception {
        Message message = clientExchange.getIn();

        Object dispatchRequestHandlerObject = message.removeHeader(DISPATCH_HANDLER_KEY);

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
