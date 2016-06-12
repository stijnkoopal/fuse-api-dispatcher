package al.koop.fuse.api.dispatcher.util;

import javax.ws.rs.*;

public interface ValidRestClass {

    String POST = "/post";
    String PUT = "/put";
    String DELETE = "/delete";
    String HEAD = "/head";
    String OPTIONS = "/options";

    @Path(PUT)
    @PUT
    void put(String putMe);

    @Path(POST)
    @POST
    void post(String postMe, String andMe);


    @Path(DELETE)
    @DELETE
    void delete(String deleteMe);

    @Path(HEAD)
    @HEAD
    void head(String heads);

    @Path(OPTIONS)
    @OPTIONS
    void options(String someOption);

}
