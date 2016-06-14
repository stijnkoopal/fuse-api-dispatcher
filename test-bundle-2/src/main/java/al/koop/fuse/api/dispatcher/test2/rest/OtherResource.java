package al.koop.fuse.api.dispatcher.test2.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@Path("/other")
public class OtherResource {

    @GET
    public Response get() {
        return ok("other.get").build();
    }
}
