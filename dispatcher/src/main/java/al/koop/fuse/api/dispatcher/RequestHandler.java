package al.koop.fuse.api.dispatcher;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;

import java.util.List;

public interface RequestHandler {

    List<RestEndpoint> getUrlMapping();

    void handle(Exchange request) throws Exception;

    CamelContext getCamelContext();
}
