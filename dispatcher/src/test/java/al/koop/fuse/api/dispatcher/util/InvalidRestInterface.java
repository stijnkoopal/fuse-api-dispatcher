package al.koop.fuse.api.dispatcher.util;

import javax.ws.rs.GET;

public interface InvalidRestInterface {
    @GET
    String securedGet();
}
