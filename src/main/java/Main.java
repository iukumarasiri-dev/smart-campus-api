import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Application entry point.
 * Starts an embedded Grizzly HTTP server and registers the JAX-RS application.
 *
 * Run with:  mvn exec:java
 * Or after packaging:  java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
 */
public class Main {

    /** Base URI the Grizzly server will listen on. */
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer() {
        // Let Jersey scan all packages for resources, providers, and filters
        final ResourceConfig config = new ResourceConfig()
                .packages("resource", "exception.mappers", "filter")
                .register(SmartCampusApp.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        System.out.println("Smart Campus API started.");
        System.out.println("Listening at: " + BASE_URI + "api/v1");
        System.out.println("Press ENTER to stop the server.");
        System.in.read();

        server.shutdownNow();
    }
}
