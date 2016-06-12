package al.koop.fuse.api.dispatcher;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public final class RestEndpoint {

    private final String uri;
    private final String method;
    private final List<Annotation> annotations;

    public RestEndpoint(String uri, String method, List<Annotation> annotations) {
        this.uri = uri;
        this.method = method;
        this.annotations = unmodifiableList(annotations);
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public List<Annotation> getAnnotations() {
        return unmodifiableList(annotations);
    }
}
