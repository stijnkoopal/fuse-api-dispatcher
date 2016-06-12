package al.koop.fuse.api.dispatcher;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_GATEWAY;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebApplicationExceptionHandlerTest {

    private WebApplicationExceptionHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new WebApplicationExceptionHandler();
    }

    @Test
    public void handlerShouldReturnResponseCodeFromException() throws Exception {
        // given
        WebApplicationException e = new WebApplicationException(Response.status(BAD_GATEWAY).build());

        // when
        int code = handler.handle(e);

        // then
        assertThat(code, is(BAD_GATEWAY.getStatusCode()));
    }

    @Test
    public void handlerShouldReturnCodeWhenCauseIsWebAppException() throws Exception {
        // given
        RuntimeException e = new RuntimeException(new WebApplicationException(Response.status(BAD_REQUEST).build()));

        // then
        int code = handler.handle(e);

        // then
        assertThat(code, is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void handlerShouldReturnCodeUpToFiveCausedDeep() throws Exception {
        // given
        Exception baseException = new RuntimeException(
                new RuntimeException(
                        new RuntimeException(
                                new RuntimeException(
                                        new RuntimeException(
                                                new WebApplicationException(Response.status(418).build())
                                        )
                                )
                        )
                )
        );

        // when
        int code = handler.handle(baseException);

        // then
        assertThat(code, is(418));
    }

    @Test
    public void handlerShouldReturn500WhenWebAppExcetionIsNotCause() throws Exception {
        // given
//        GenericException e = new GenericException();
//
//        // when
//        int code = handler.handle(e);
//
//        // then
//        assertThat(code, is(500));
    }

    @Test
    public void handlerShouldReturn500WhenCauseIsToDeep() throws Exception {
        // given
        Exception baseException = new RuntimeException(
                new RuntimeException(
                        new RuntimeException(
                                new RuntimeException(
                                        new RuntimeException(
                                                new RuntimeException(
                                                        new WebApplicationException(Response.status(418).build())
                                                )
                                        )
                                )
                        )
                )
        );

        // when
        int code = handler.handle(baseException);

        // then
        assertThat(code, is(500));

    }
}