package al.koop.fuse.api.dispatcher.routes.rest;

public class RestInterfaceClass implements RestInterface {
    @Override
    public String loginWithoutRole() {
        return "login without role";
    }

    @Override
    public String loginMain() {
        return "login with role main";
    }

    @Override
    public String loginSub() {
        return "login with role sub";
    }

    @Override
    public String loginMainSub() {
        return "login with roles main and sub";
    }

    @Override
    public String loginUnsecured() {
        return "unsecured";
    }
}
