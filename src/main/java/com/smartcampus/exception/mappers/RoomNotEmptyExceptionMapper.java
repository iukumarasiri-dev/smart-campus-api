package com.smartcampus.exception.mappers;

import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Room Conflict");
        error.put("message", exception.getMessage());
        error.put("status", "409");

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
