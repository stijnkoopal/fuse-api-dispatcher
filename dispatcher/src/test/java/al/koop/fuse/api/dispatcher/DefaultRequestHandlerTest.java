package al.koop.fuse.api.dispatcher;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.ExplicitCamelContextNameStrategy;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class DefaultRequestHandlerTest {

    RequestHandler handler;

    @Test(expected = IllegalArgumentException.class)
    public void constructorBeanNullException() throws IllegalArgumentException{
        handler = new DefaultRequestHandler("/", new DefaultCamelContext() { }, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorContextNullException() throws IllegalArgumentException{
        handler = new DefaultRequestHandler("/", null, new JAXRSServerFactoryBean());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorEndpointNullException() throws IllegalArgumentException{
        handler = new DefaultRequestHandler(null, new DefaultCamelContext(), new JAXRSServerFactoryBean());
    }

    @Test
    public void constructorCannotFindEndpointException() throws IllegalArgumentException{
        try {
            handler = new DefaultRequestHandler("/someEndpoint", new DefaultCamelContext() { }, new JAXRSServerFactoryBean());
        }catch (IllegalArgumentException e){
            assertEquals("Cannot find Endpoint for uri '/someEndpoint'", e.getMessage());
        }
    }

    @Test
    public void createSupportedUrls() {
        try {
            CamelContext context = new DefaultCamelContext();
            context.setNameStrategy(new ExplicitCamelContextNameStrategy("TestContextName"));
            org.apache.camel.Endpoint mockEndpoint = mock(org.apache.camel.Endpoint.class);
            context.addEndpoint("/test", mockEndpoint);
            handler = new DefaultRequestHandler("/test:1", context, new JAXRSServerFactoryBean());
            assertEquals("TestContextName", handler.getCamelContext().getName());
            assertEquals(0, handler.getUrlMapping().size());
        }catch (Exception e){
            fail("Unexpected exception");
        }
    }

}