package al.koop.fuse.api.dispatcher.util;

import org.apache.camel.Message;
import org.apache.camel.PropertyInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanupResponse {
    private static final Logger LOG = LoggerFactory.getLogger(CleanupResponse.class);

    @PropertyInject("al.koop.fuse.api.dispatcher.clean.response.removeHeaders")
    private String removeHeaders;

    @PropertyInject("al.koop.fuse.api.dispatcher.clean.response.removeHeaders.exclude")
    private String excludeHeaders;

    public void clean(boolean cleanupBody, Message message) {
        LOG.debug("Entering cleanup, body clean: {} ", cleanupBody);
        message.removeHeaders(removeHeaders, excludeHeaders);

        if (cleanupBody) {
            message.setBody(null);
        }
    }

    public void cleanWithContentType(boolean cleanupBody, String contentType, Message message) {
        LOG.debug("Entering cleanup, body clean: {} ", cleanupBody);
        message.removeHeaders(removeHeaders, excludeHeaders);
        message.setHeader("Content-Type", contentType);

        if (cleanupBody) {
            message.setBody(null);
        }
    }
}