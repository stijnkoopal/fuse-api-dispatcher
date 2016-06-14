package al.koop.fuse.api.dispatcher.internal;

import al.koop.fuse.api.dispatcher.RequestHandler;
import al.koop.fuse.api.dispatcher.RestEndpoint;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.model.URITemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

import static java.util.Collections.sort;
import static java.util.Collections.synchronizedList;
import static org.apache.cxf.jaxrs.model.URITemplate.createTemplate;

public class RequestHandlerRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(RequestHandlerRegistry.class);

    private static class Tuple<T, U> {

        private static final Comparator<Tuple<URITemplate, HandlerWrapper>> COMPARATOR = (t1, t2) -> {
            int left = t1.getLeft().getVariables().size();
            int right = t2.getLeft().getVariables().size();

            return left == right ? 0 : left < right ? -1 : 1;
        };

        private final T left;
        private final U right;

        Tuple(T left, U right) {
            this.left = left;
            this.right = right;
        }

        T getLeft() {
            return left;
        }

        U getRight() {
            return right;
        }
    }

    private final List<Tuple<URITemplate, HandlerWrapper>> dispatchers = synchronizedList(new ArrayList<>());
    private final String baseUri;

    public RequestHandlerRegistry(String baseUri) {
        this.baseUri = baseUri;
    }

    public Optional<HandlerWrapper> findDispatcher(String uri, String method) {
        return dispatchers.stream()
                .filter(p -> p.getLeft().match(uri, new MetadataMap<>()))
                .filter(p -> p.getRight().getMethod().equals(method))
                .map(Tuple::getRight)
                .findFirst();
    }

    public Optional<URITemplate> findMatchingTemplate(String uri, MultivaluedMap<String, String> parameterValues) {
        return dispatchers.stream()
                .map(Tuple::getLeft)
                .filter(p -> p.match(uri, parameterValues))
                .findFirst();
    }

    public synchronized void bind(RequestHandler requestHandler, Map properties) {
        String blueprintServiceName = (String) properties.get("osgi.service.blueprint.compname");
        LOG.info("Binding RequestHandler for blueprint name: {}", blueprintServiceName);

        if (requestHandler.getUrlMapping() != null && !requestHandler.getUrlMapping().isEmpty()) {
            for (RestEndpoint restEndpoint : requestHandler.getUrlMapping()) {
                String url = normalizeUrl(restEndpoint.getUri());

                URITemplate template = createTemplate(url);

                LOG.debug("Added new dispatcher {} to uri {}", blueprintServiceName, url);
                dispatchers.add(new Tuple<>(template, new HandlerWrapper(requestHandler,
                        restEndpoint.getMethod(), restEndpoint.getAnnotations())));
            }

            // We can do this, as it is initialisation time, and not a lot of calls are made to this method
            // otherwise, we would need some ordered collection; that however, posses other problems
            sort(dispatchers, Tuple.COMPARATOR);

        } else {
            LOG.warn("No supported urls given for '{}'", blueprintServiceName);
        }
    }

    public synchronized void unbind(RequestHandler requestHandler, Map properties) {
        // Unbind takes places when shutting down bundle, but with null value
        if (requestHandler == null) {
            return;
        }

        String blueprintServiceName = (String) properties.get("osgi.service.blueprint.compname");
        LOG.info("Unbinding RequestHandler for blueprint name: {}", blueprintServiceName);

        if (requestHandler.getUrlMapping() != null && !requestHandler.getUrlMapping().isEmpty()) {
            for (RestEndpoint restEndpoint : requestHandler.getUrlMapping()) {
                Iterator<Tuple<URITemplate, HandlerWrapper>> iterator = dispatchers.iterator();
                String url = normalizeUrl(restEndpoint.getUri());
                while (iterator.hasNext()) {
                    URITemplate template = iterator.next().getLeft();
                    int compare = URITemplate.compareTemplates(template, createTemplate(url));
                    if (compare == 0) {
                        iterator.remove();

                        LOG.debug("Remove dispatcher {} from uri {}", blueprintServiceName, url);
                        break;
                    }
                }
            }
        }
    }

    public Collection<Tuple<URITemplate, HandlerWrapper>> getDispatchers() {
        return dispatchers;
    }

    private String normalizeUrl(String url) {
        String baseUri = getBaseUri();
        if (!url.startsWith(baseUri)) {
            url = baseUri + url;
        }

        return url;
    }

    private String getBaseUri() {
        if (baseUri == null) {
            return "";
        }

        if (baseUri.endsWith("/")) {
            return baseUri.substring(0, baseUri.length() - 1);
        }

        return baseUri;
    }
}
