package al.koop.fuse.api.dispatcher.util;

import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static junit.framework.Assert.*;

/**
 *
 */
public class ResponseCleanerTest {

    @InjectMocks
    private ResponseCleaner cr = new ResponseCleaner("*", "CamelHttpResponseCode");
    private Message message;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        message = new DefaultMessage();
        message.setHeader("testHeader", "with a value of a string");
        message.setHeader("CamelHttpResponseCode", "This header should not be removed");
        message.setBody(new Object());
    }

    @Test
    public void cleandHeaderOnly(){

        assertEquals(2, message.getHeaders().size());
        cr.clean(message);
        assertEquals(1, message.getHeaders().size());
        assertFalse(message.getHeaders().containsKey("testHeader"));
        assertNotNull(message.getBody());
    }
}