package filter;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Logger;

/**
 * JAX-RS container filter that logs each incoming request and its response status.
 * Registered automatically via {@code @Provider}.
 */
@Provider
@PreMatching
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    private static final String START_TIME_PROPERTY = "filter.startTime";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());

        LOG.info(String.format("→ %s %s  [%s]",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                Instant.now()));
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        long start = (long) requestContext.getProperty(START_TIME_PROPERTY);
        long elapsed = System.currentTimeMillis() - start;

        LOG.info(String.format("← %s %s  status=%d  (%d ms)",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus(),
                elapsed));
    }
}
