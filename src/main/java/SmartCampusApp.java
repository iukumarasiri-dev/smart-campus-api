import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS application entry point.
 * All REST resources are mounted under /api/v1.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApp extends Application {
    // No additional configuration required;
    // JAX-RS auto-discovers resource and provider classes.
}
