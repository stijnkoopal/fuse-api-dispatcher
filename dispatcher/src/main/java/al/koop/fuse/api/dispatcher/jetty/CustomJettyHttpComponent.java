package al.koop.fuse.api.dispatcher.jetty;

import org.apache.camel.component.jetty8.JettyHttpComponent8;
import org.eclipse.jetty.server.Server;

/**
 *
 */
public class CustomJettyHttpComponent extends JettyHttpComponent8 {

    @Override
    protected Server createServer()  {
        final Server server = super.createServer();
        server.addBean(new CustomErrorHandler());
        server.setSendServerVersion(false);
        return server;
    }
}
