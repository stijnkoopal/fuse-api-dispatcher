package al.koop.fuse.api.dispatcher.jetty;

import org.eclipse.jetty.server.handler.ErrorHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

/**
 *
 */
public class CustomErrorHandler extends ErrorHandler {

    @Override
    protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
        writer.write("<h1>" + code + "</h1>");
    }

}
