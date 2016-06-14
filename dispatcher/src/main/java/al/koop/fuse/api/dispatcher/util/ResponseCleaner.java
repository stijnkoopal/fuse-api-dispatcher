package al.koop.fuse.api.dispatcher.util;

import org.apache.camel.Message;

public class ResponseCleaner {
    private final String removeHeaders;
    private final String excludeHeaders;

    public ResponseCleaner(String removeHeaders, String excludeHeaders) {
        this.removeHeaders = removeHeaders;
        this.excludeHeaders = excludeHeaders;
    }

    public void clean(Message message) {
        message.removeHeaders(removeHeaders, excludeHeaders);
    }
}