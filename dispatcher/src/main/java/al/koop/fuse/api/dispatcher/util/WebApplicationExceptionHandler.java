package al.koop.fuse.api.dispatcher.util;

import org.slf4j.Logger;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * In order to return a different status, throw an WebApplicationException
 */
public class WebApplicationExceptionHandler {
    private static final Logger LOG = getLogger(WebApplicationExceptionHandler.class);

    public int handle(Exception e) {
        LOG.debug("Exception caught {}", e.getMessage());
        LOG.debug("{}", e);

        if (e instanceof WebApplicationException){
            return ((WebApplicationException)e).getResponse().getStatus();
        } else {
            Throwable cause = e.getCause();
            int i = 0;
            while (cause != null && i < 5) {
                if (cause instanceof WebApplicationException) {
                    return ((WebApplicationException) cause).getResponse().getStatus();
                }

                i++;
                cause = cause.getCause();
            }
            return INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }

}
