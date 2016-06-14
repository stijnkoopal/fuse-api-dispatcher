package al.koop.fuse.api.dispatcher.test1.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@Path("/bar")
public class BarResource {

    @GET
    public Response get() {
        return ok("bar.get").build();
    }

    @POST
    public Response post() {
        return ok("bar.post").build();
    }
}
