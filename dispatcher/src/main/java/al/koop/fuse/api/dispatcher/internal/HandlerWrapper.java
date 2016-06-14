package al.koop.fuse.api.dispatcher.internal;

import al.koop.fuse.api.dispatcher.RequestHandler;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class HandlerWrapper {
    private final RequestHandler requestHandler;
    private final String method;
    private final List<Annotation> annotations;

    public HandlerWrapper(RequestHandler requestHandler, String method, List<Annotation> annotations) {
        this.requestHandler = requestHandler;
        this.method = method;
        this.annotations = unmodifiableList(annotations);
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public String getMethod() {
        return method;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }
}
