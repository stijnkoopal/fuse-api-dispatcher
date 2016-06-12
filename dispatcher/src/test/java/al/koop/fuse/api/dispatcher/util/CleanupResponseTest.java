package al.koop.fuse.api.dispatcher.util;

import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class CleanupResponseTest {

    @InjectMocks
    private CleanupResponse cr = new CleanupResponse();
    private Message message;
    private String contentType = "test";

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Field removeHeadersField = cr.getClass().getDeclaredField("removeHeaders");
        Field excludeHeadersField = cr.getClass().getDeclaredField("excludeHeaders");
        removeHeadersField.setAccessible(true);
        excludeHeadersField.setAccessible(true);
        removeHeadersField.set(cr, "*");
        excludeHeadersField.set(cr, "CamelHttpResponseCode");
        message = new DefaultMessage();
        message.setHeader("testHeader", "with a value of a string");
        message.setHeader("CamelHttpResponseCode", "This header should not be removed");
        message.setBody(new Object());
    }

    @Test
    public void cleanBodyAndHeader(){

        assertEquals(2, message.getHeaders().size());
        cr.clean(true, message);
        assertEquals(1, message.getHeaders().size());
        assertFalse(message.getHeaders().containsKey("testHeader"));
        assertNull(message.getBody());
    }

    @Test
    public void cleandHeaderOnly(){

        assertEquals(2, message.getHeaders().size());
        cr.clean(false, message);
        assertEquals(1, message.getHeaders().size());
        assertFalse(message.getHeaders().containsKey("testHeader"));
        assertNotNull(message.getBody());
    }


    @Test
    public void cleanBodyAndHeaderWithContentType(){

        assertEquals(2, message.getHeaders().size());
        cr.cleanWithContentType(true, contentType, message);
        assertFalse(message.getHeaders().containsKey("testHeader"));
        assertEquals(2, message.getHeaders().size());
        assertNull(message.getBody());
        assertEquals(contentType, message.getHeader("Content-Type"));
    }

    @Test
    public void cleandHeaderOnlyWithContentType(){

        assertEquals(2, message.getHeaders().size());
        cr.cleanWithContentType(false, contentType, message);
        assertFalse(message.getHeaders().containsKey("testHeader"));
        assertEquals(2, message.getHeaders().size());
        assertNotNull(message.getBody());
        assertEquals(contentType, message.getHeader("Content-Type"));
    }

}