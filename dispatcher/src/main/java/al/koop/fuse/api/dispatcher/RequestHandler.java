package al.koop.fuse.api.dispatcher;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;

import java.util.List;

public interface RequestHandler {

    /**
     * The URL mapping for the given handler. Contains an endpoint for every cxf annotated method found
     * @return the URL mapping
     */
    List<RestEndpoint> getUrlMapping();

    /**
     * Handle the request by this handler
     *
     * @param request The request to be handled
     * @throws Exception
     */
    void handle(Exchange request) throws Exception;

    /**
     * As we are using features from another bundle, we require its camel context
     *
     * @return the camel context this bundle runs in
     */
    CamelContext getCamelContext();
}
