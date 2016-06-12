package al.koop.fuse.api.dispatcher.jetty;

import org.apache.camel.component.http.HttpHeaderFilterStrategy;

/**
 *
 */
public class CustomHeaderFilterStrategy extends HttpHeaderFilterStrategy {
    @Override
    protected void initialize() {
        // filters must be lowercase
        getOutFilter().add("breadcrumbid");
        super.initialize();
    }
}
