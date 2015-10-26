import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by Paul Bernard on 10/21/15.
 */
public class Boot {

    public static void main(String[] args) throws Exception {

        final int port = Integer.parseInt(System.getProperty("port", "8080"));
        final String home = System.getProperty("home","");
        Server server = new Server(port);
        ProtectionDomain domain = Boot.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();
        WebAppContext webapp = new WebAppContext();
        //webapp.setInitParameter("contextConfigLocation", "classpath*:WEB-INF/eventservice-servlet.xml");
        webapp.setContextPath("/");

        //ServletContextHandler contextHandler = new ServletContextHandler();
        //contextHandler.setErrorHandler(null);
        //contextHandler.setContextPath("/");

        if (home.length() != 0) {
            webapp.setTempDirectory(new File(home));
        }
        webapp.setWar(location.toExternalForm());
        server.setHandler(webapp);
        //server.setHandler(contextHandler);
        server.start();
        server.join();

    }



}
