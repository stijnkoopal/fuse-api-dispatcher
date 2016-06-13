package al.koop.fuse.api.dispatcher.test1.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@Path("/foo")
public class FooResource {

    @GET
    public Response get() {
        return ok().build();
    }

}
