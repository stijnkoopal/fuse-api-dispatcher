package al.koop.fuse.api.dispatcher.internal;

import al.koop.fuse.api.dispatcher.RequestHandler;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static al.koop.fuse.api.dispatcher.internal.DispatchHandler.DISPATCH_HANDLER_HEADER;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class DispatchHandlerTest {

    @Mock
    private RequestHandlerRegistry requestHandlerRegistryMock;

    @Mock
    private Exchange exchangeMock;

    @Mock
    private HttpMessage messageMock;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private RequestHandler requestHandlerMock;

    @Mock
    private CamelContext camelContextMock;

    @InjectMocks
    private DispatchHandler dispatchHandler;

    @Before
    public void setUp() {
        given(exchangeMock.getIn()).willReturn(messageMock);
        given(messageMock.getRequest()).willReturn(requestMock);
    }

    @Test(expected = NotFoundException.class)
    public void resolveShouldHaveDispatcher() throws Exception {
        // given
        given(requestHandlerRegistryMock.findDispatcher(anyString(), anyString())).willReturn(empty());

        // when
        dispatchHandler.resolve(exchangeMock);
    }

    @Test
    public void securedAsksForProfile() throws Exception {
        // given
        HandlerWrapper handlerWrapper = new HandlerWrapper(requestHandlerMock, "GET", emptyList());
        given(requestHandlerRegistryMock.findDispatcher(anyString(), anyString())).willReturn(Optional.of(handlerWrapper));

        // when
        dispatchHandler.resolve(exchangeMock);

        // then
        then(messageMock).should(times(1)).setHeader(DISPATCH_HANDLER_HEADER, handlerWrapper);
    }

    @Test(expected = IllegalStateException.class)
    public void dispatcherShouldBeSet() throws Exception{
        // given
        given(messageMock.removeHeader(DISPATCH_HANDLER_HEADER)).willReturn(null);

        // when
        dispatchHandler.dispatch(exchangeMock);

        // then
        then(requestHandlerMock).should(times(0)).handle(any(Exchange.class));
    }

    @Test
    public void dispatchShouldDispatchWithClonedExchange() throws Exception{
        // given
        HandlerWrapper handlerWrapper = new HandlerWrapper(requestHandlerMock, "GET", emptyList());
        given(messageMock.removeHeader(DISPATCH_HANDLER_HEADER)).willReturn(handlerWrapper);
        given(requestHandlerMock.getCamelContext()).willReturn(camelContextMock);

        // when
        dispatchHandler.dispatch(exchangeMock);

        // then
        ArgumentCaptor<Exchange> captor = ArgumentCaptor.forClass(Exchange.class);
        then(requestHandlerMock).should(times(1)).handle(captor.capture());

        assertThat(captor.getValue(), is(notNullValue()));
        assertThat(captor.getValue(), not(is(exchangeMock)));
    }
}