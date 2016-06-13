package al.koop.fuse.api.dispatcher.util;

import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanupResponse {
    private static final Logger LOG = LoggerFactory.getLogger(CleanupResponse.class);

    private final String removeHeaders;
    private final String excludeHeaders;

    public CleanupResponse(String removeHeaders, String excludeHeaders) {
        this.removeHeaders = removeHeaders;
        this.excludeHeaders = excludeHeaders;
    }

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