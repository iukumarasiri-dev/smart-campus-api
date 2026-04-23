package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.DataStore;
import com.smartcampus.model.Room;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // Get shared data store
    private final DataStore store = DataStore.getInstance();

    // ─────────────────────────────────────────
    // GET /api/v1/rooms → Get all rooms
    // ─────────────────────────────────────────
    @GET
    public Response getAllRooms() {
        List<Room> rooms = new ArrayList<>(store.rooms.values());
        return Response.ok(rooms).build();
    }

    // ─────────────────────────────────────────
    // POST /api/v1/rooms → Create a new room
    // ─────────────────────────────────────────
    @POST
    public Response createRoom(Room room) {
        // Check if room ID already exists
        if (store.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Room with this ID already exists\"}")
                    .build();
        }

        // Check required fields
        if (room.getId() == null || room.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Room ID and Name are required\"}")
                    .build();
        }

        // Save room
        store.rooms.put(room.getId(), room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // ─────────────────────────────────────────
    // GET /api/v1/rooms/{roomId} → Get one room
    // ─────────────────────────────────────────
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.rooms.get(roomId);

        // If room not found
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found with ID: " + roomId + "\"}")
                    .build();
        }

        return Response.ok(room).build();
    }

    // ─────────────────────────────────────────
    // DELETE /api/v1/rooms/{roomId} → Delete room
    // ─────────────────────────────────────────
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.rooms.get(roomId);

        // If room not found
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found with ID: " + roomId + "\"}")
                    .build();
        }

        // ⚠️ Business Logic - Block deletion if room has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room " + roomId + " cannot be deleted as it still has " +
                room.getSensorIds().size() + " sensor(s) assigned to it."
            );
        }

        // Delete the room
        store.rooms.remove(roomId);

        return Response.ok()
                .entity("{\"message\": \"Room " + roomId + " deleted successfully\"}")
                .build();
    }
}
