package al.koop.fuse.api.dispatcher.internal;

import al.koop.fuse.api.dispatcher.RequestHandler;
import al.koop.fuse.api.dispatcher.RestEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerRegistryTest {

    @Mock
    private RequestHandler requestHandlerMock;

    @InjectMocks
    private RequestHandlerRegistry requestHandlerRegistry;

    @Test
    public void emptyRegistryFindsNothing() {
        // when
        Optional<HandlerWrapper> handlerWrapper = requestHandlerRegistry.findDispatcher("bla", "GET");

        // then
        assertThat(handlerWrapper, is(notNullValue()));
        assertThat(handlerWrapper.isPresent(), is(false));
    }

    @Test
    public void additionAddsToRegistery() {
        // given
        given(requestHandlerMock.getUrlMapping()).willReturn(singletonList(new RestEndpoint("/test", "GET", emptyList())));
        requestHandlerRegistry.bind(requestHandlerMock, singletonMap("osgi.service.blueprint.compname", "test"));

        // when
        Optional<HandlerWrapper> handlerWrapper = requestHandlerRegistry.findDispatcher("/test", "GET");

        // then
        assertThat(handlerWrapper, is(notNullValue()));
        assertThat(handlerWrapper.isPresent(), is(true));

        assertThat(handlerWrapper.get().getRequestHandler(), is(requestHandlerMock));
    }

    @Test
    public void removalRemovesFromRegistery() {
        // given
        given(requestHandlerMock.getUrlMapping()).willReturn(singletonList(new RestEndpoint("/test", "GET", emptyList())));
        requestHandlerRegistry.bind(requestHandlerMock, singletonMap("osgi.service.blueprint.compname", "test"));
        requestHandlerRegistry.unbind(requestHandlerMock, singletonMap("osgi.service.blueprint.compname", "test"));

        // when
        Optional<HandlerWrapper> handlerWrapper = requestHandlerRegistry.findDispatcher("/test", "GET");

        // then
        assertThat(handlerWrapper, is(notNullValue()));
        assertThat(handlerWrapper.isPresent(), is(false));
    }

    @Test
    public void allEndpointsBound() {
        // given
        List<RestEndpoint> mapping = asList(new RestEndpoint("a{", "GET", emptyList()), new RestEndpoint("b{", "GET", emptyList()));
        given(requestHandlerMock.getUrlMapping()).willReturn(mapping);

        // when
        requestHandlerRegistry.bind(requestHandlerMock, singletonMap("osgi.service.blueprint.compname", "test"));

        // then
        assertThat(requestHandlerRegistry.getDispatchers(), is(notNullValue()));
        assertThat(requestHandlerRegistry.getDispatchers().size(), is(2));
    }

    @Test
    public void properEndpointSelected() {
        // given
        List<RestEndpoint> mapping = asList(new RestEndpoint("a/{c}", "GET", emptyList()), new RestEndpoint("a/c", "GET", emptyList()), new RestEndpoint("a/{b}", "GET", emptyList()));
        given(requestHandlerMock.getUrlMapping()).willReturn(mapping);
        requestHandlerRegistry.bind(requestHandlerMock, singletonMap("osgi.service.blueprint.compname", "test"));

        // when
        Optional<HandlerWrapper> handlerWrapper = requestHandlerRegistry.findDispatcher("/a/c", "GET");

        // then
        assertThat(handlerWrapper, is(notNullValue()));
        assertThat(handlerWrapper.isPresent(), is(true));
    }
}