package al.koop.fuse.api.dispatcher;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static al.koop.fuse.api.dispatcher.RestEndpointFinder.findRESTEndpoints;
import static java.util.Collections.unmodifiableList;

public class DefaultRequestHandler implements RequestHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRequestHandler.class);

    private final List<RestEndpoint> urls = new ArrayList<RestEndpoint>();
    private final ProducerTemplate producerTemplate;
    private final CamelContext camelContext;

    public DefaultRequestHandler(String endpointUri, CamelContext camelContext, JAXRSServerFactoryBean restBeansServer) {
        if (camelContext == null || endpointUri == null || restBeansServer == null) {
            throw new IllegalArgumentException("One or more required constructor fields are not filled");
        }

        this.camelContext = camelContext;
        this.producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint(endpointUri);

        if (endpoint == null) {
            throw new IllegalArgumentException("Cannot find Endpoint for uri '" + endpointUri + "'");
        }

        this.producerTemplate.setDefaultEndpoint(endpoint);

        resolveAndAddEndpoints(restBeansServer);
    }

    @Override
    public List<RestEndpoint> getUrlMapping() {
        return unmodifiableList(urls);
    }

    @Override
    public void handle(Exchange request) throws Exception {
        producerTemplate.send(request);
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    private void resolveAndAddEndpoints(JAXRSServerFactoryBean restBeansServer) {
        LOG.debug("create urls");
        try {
            findRESTEndpoints(restBeansServer)
                    .forEach(urls::add);

        } catch (Exception e) {
            LOG.warn("Cannot add urls for REST server '{}', cause '{}'", restBeansServer.getBindingId(), e.toString());
            LOG.debug("", e);
            throw new IllegalStateException("Cannot add urls for REST server, REST server: " + restBeansServer.getBindingId(), e);
        }
    }
}
