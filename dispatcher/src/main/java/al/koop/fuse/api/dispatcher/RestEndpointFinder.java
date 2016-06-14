package al.koop.fuse.api.dispatcher;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.stream.Stream.concat;
import static javax.ws.rs.HttpMethod.*;
import static org.slf4j.LoggerFactory.getLogger;

public class RestEndpointFinder {
    private static final Logger LOG = getLogger(RestEndpointFinder.class);

    public static Stream<RestEndpoint> findRESTEndpoints(JAXRSServerFactoryBean restServer) throws Exception {
        List<Class<?>> classes = restServer.getServiceFactory().getResourceClasses();

        return classes.stream()
                .flatMap(c -> concat(concat(stream(c.getInterfaces()), stream(c.getClasses())), Stream.of(c)))
                .flatMap(RestEndpointFinder::checkClass);
    }

    public static Stream<RestEndpoint> findRESTEndpoints(String basePath, Class<?> clazz) {
        return stream(clazz.getMethods())
                .map(m -> createEndpoint(m, basePath))
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty));
    }

    private static Stream<RestEndpoint> checkClass(Class<?> clazz) {
        Annotation annotation = clazz.getAnnotation(Path.class);
        String rootPath = "/";
        if (annotation != null) {
            rootPath = getRESTEndpointPath(clazz);
        }
        return findRESTEndpoints(rootPath, clazz);
    }

    /**
     * Create an endpoint object to represent the REST endpoint defined in the
     * specified Java method.
     */
    private static Optional<RestEndpoint> createEndpoint(Method javaMethod, String classUri) {
        if (isRestMethod(javaMethod)) {
            String httpMethod = getMethod(javaMethod);
            Path path = javaMethod.getAnnotation(Path.class);
            String uri = path != null ? classUri + path.value() : classUri;

            return Optional.of(new RestEndpoint(uri, httpMethod, asList(javaMethod.getAnnotations())));
        }
        LOG.debug("Method not a rest method: " + javaMethod.getName());
        return empty();
    }

    private static boolean isRestMethod(Method method) {
        return getMethod(method) != null;
    }

    private static String getMethod(Method method) {
        if (isGetMethod(method)) {
            return GET;
        } else if (isPutMethod(method)) {
            return PUT;
        } else if (isPostMethod(method)) {
            return POST;
        } else if (isDeleteMethod(method)) {
            return DELETE;
        } else if (isHeadMethod(method)) {
            return HEAD;
        } else if (isOptionsMethod(method)) {
            return OPTIONS;
        }
        return null;
    }

    private static boolean isGetMethod(Method method) {
        return method.isAnnotationPresent(GET.class);
    }

    private static boolean isPutMethod(Method method) {
        return method.isAnnotationPresent(PUT.class);
    }

    private static boolean isPostMethod(Method method) {
        return method.isAnnotationPresent(POST.class);
    }

    private static boolean isDeleteMethod(Method method) {
        return method.isAnnotationPresent(DELETE.class);
    }

    private static boolean isHeadMethod(Method method) {
        return method.isAnnotationPresent(HEAD.class);
    }

    private static boolean isOptionsMethod(Method method) {
        return method.isAnnotationPresent(OPTIONS.class);
    }

    /**
     * Get the REST endpoint path for the specified class. Getting the path for
     * that class before appending the location in the @Path annotation.
     */
    private static String getRESTEndpointPath(Class<?> clazz) {
        String path = "";
        if (clazz != null) {
            Annotation annotation = clazz.getAnnotation(Path.class);
            if (annotation != null) {
                path = ((Path) annotation).value() + path;
            }
        }
        return path;
    }
}
