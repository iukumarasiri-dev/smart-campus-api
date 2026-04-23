package com.smartcampus.exception.mappers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(
        GlobalExceptionMapper.class.getName()
    );

    @Override
    public Response toResponse(Throwable exception) {

        // Handle Jersey's own exceptions (404, 405, etc.) — return JSON with correct status
        if (exception instanceof WebApplicationException) {
            int status = ((WebApplicationException) exception).getResponse().getStatus();
            String reason = Response.Status.fromStatusCode(status) != null
                    ? Response.Status.fromStatusCode(status).getReasonPhrase()
                    : "Error";
            Map<String, String> error = new HashMap<>();
            error.put("error", reason);
            error.put("message", "The requested resource was not found.");
            error.put("status", String.valueOf(status));
            return Response.status(status)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Log the full error on server side only
        LOGGER.severe("Unexpected error occurred: " + exception.getMessage());

        // Return safe generic message to client
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please try again later.");
        error.put("status", "500");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
