package al.koop.fuse.api.dispatcher.routes;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.cxf.helpers.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;

@Ignore
public class RouteTest extends CamelBlueprintTestSupport {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testDispatcher() throws IOException {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        final CloseableHttpClient client = builder.build();

        final HttpGet get = new HttpGet("http://localhost:8123/api/login");

        final CloseableHttpResponse response = client.execute(get);
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), is("hoi"));
    }

    /**
     * If we start the tests without these filters, we will also start all referenced bundles
     * as they are referenced from the Manifest file.
     * When those bundles get loaded, they will be looking for properties in their own bungle context.
     *
     * @return
     */
    @Override
    protected String getBundleFilter() {
        return "(&(Bundle-SymbolicName=*)(!(Bundle-SymbolicName=nl.travelcard.integration.api-dispatcher)))";
    }

    /**
     * Do not include osgi.xml, as this file will look for bundles.
     *
     * @return
     */
    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/beans.xml, OSGI-INF/blueprint/camelContext.xml, test-cxf.xml, test-osgi.xml";
    }

    @Override
    protected String[] loadConfigAdminConfigurationFile() {
        return new String[] {"nl.travelcard.integration.api.dispatcher", "nl.travelcard.integration.api.dispatcher.cfg"};
    }
}
