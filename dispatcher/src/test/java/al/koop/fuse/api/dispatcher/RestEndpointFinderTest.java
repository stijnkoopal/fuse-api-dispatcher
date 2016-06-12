package al.koop.fuse.api.dispatcher;

import al.koop.fuse.api.dispatcher.util.InvalidRestInterface;
import al.koop.fuse.api.dispatcher.util.ValidRestClass;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.JAXRSServiceFactoryBean;
import org.junit.Ignore;
import org.junit.Test;

import java.util.stream.Stream;

import static al.koop.fuse.api.dispatcher.util.ValidRestClass.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RestEndpointFinderTest {

    @Test
    public void testFindRESTEndpoints() throws Exception {
        // given and when
        Stream<RestEndpoint> endpoints = RestEndpointFinder.findRESTEndpoints("", ValidRestClass.class);

        // then
        thenEndpointsShouldBeMapped(endpoints);
    }

    @Test
    @Ignore
    public void testFindRESTEndPointByRestServer() throws Exception {
        // given
        JAXRSServiceFactoryBean sf = new JAXRSServiceFactoryBean();
        sf.setResourceClass(ValidRestClass.class);
        JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean(sf);

        // when
        Stream<RestEndpoint> endpoints = RestEndpointFinder.findRESTEndpoints(factoryBean);

        // then
        thenEndpointsShouldBeMapped(endpoints);
    }

    @Test
    public void testFindRestEnpointsInvalidAnnotation() throws Exception {
        Stream<RestEndpoint> restEndpoints = RestEndpointFinder.findRESTEndpoints("/", InvalidRestInterface.class);
        assertThat(restEndpoints.count(), is(1L));
    }

    public void thenEndpointsShouldBeMapped(Stream<RestEndpoint> endpoints) {
        endpoints.forEach(endpoint -> {
            switch (endpoint.getUri()) {
                case PUT:
                    assertThat(endpoint.getMethod(), is("PUT"));
                    break;
                case POST:
                    assertThat(endpoint.getMethod(), is("POST"));
                    break;
                case DELETE:
                    assertThat(endpoint.getMethod(), is("DELETE"));
                    break;
                case HEAD:
                    assertThat(endpoint.getMethod(), is("HEAD"));
                    break;
                case OPTIONS:
                    assertThat(endpoint.getMethod(), is("OPTIONS"));
                    break;
                default:
                    assertTrue("Endpoints should match one of the defined", false);
            }
        });
    }
}