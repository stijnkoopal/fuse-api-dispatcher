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

import static al.koop.fuse.api.dispatcher.internal.DispatchingHelper.DISPATCH_HANDLER_KEY;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class RequestDispatcherTest {

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
    private RequestDispatcher requestDispatcher;

    @Before
    public void setUp() {
        given(exchangeMock.getIn()).willReturn(messageMock);
        given(messageMock.getRequest()).willReturn(requestMock);
    }

    @Test(expected = IllegalStateException.class)
    public void dispatcherShouldBeSet() throws Exception{
        // given
        given(messageMock.removeHeader(DISPATCH_HANDLER_KEY)).willReturn(null);

        // when
        requestDispatcher.dispatch(exchangeMock);

        // then
        then(requestHandlerMock).should(times(0)).handle(any(Exchange.class));
    }

    @Test
    public void dispatchShouldDispatchWithClonedExchange() throws Exception{
        // given
        HandlerWrapper handlerWrapper = new HandlerWrapper(requestHandlerMock, "GET", emptyList());
        given(messageMock.removeHeader(DISPATCH_HANDLER_KEY)).willReturn(handlerWrapper);
        given(requestHandlerMock.getCamelContext()).willReturn(camelContextMock);

        // when
        requestDispatcher.dispatch(exchangeMock);

        // then
        ArgumentCaptor<Exchange> captor = ArgumentCaptor.forClass(Exchange.class);
        then(requestHandlerMock).should(times(1)).handle(captor.capture());

        assertThat(captor.getValue(), is(notNullValue()));
        assertThat(captor.getValue(), not(is(exchangeMock)));
    }
}