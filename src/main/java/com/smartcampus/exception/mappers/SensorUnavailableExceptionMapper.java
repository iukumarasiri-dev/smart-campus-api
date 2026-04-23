package com.smartcampus.exception.mappers;

import com.smartcampus.exception.SensorUnavailableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Sensor Unavailable");
        error.put("message", exception.getMessage());
        error.put("status", "403"); 

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
