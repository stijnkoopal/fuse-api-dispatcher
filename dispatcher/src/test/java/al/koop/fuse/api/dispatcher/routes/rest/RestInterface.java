package al.koop.fuse.api.dispatcher.routes.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface RestInterface {

    @Path("/loginWithoutRole")
    @GET
    String loginWithoutRole();

    @Path("/loginRoleMain")
    @GET
    String loginMain();

    @Path("/loginRoleSub")
    @GET
    String loginSub();

    @Path("/loginRoleMainSub")
    @GET
    String loginMainSub();

    @Path("/loginUnsecured")
    @GET
    String loginUnsecured();
}
