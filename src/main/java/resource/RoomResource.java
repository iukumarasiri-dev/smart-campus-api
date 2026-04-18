package resource;

import exception.RoomNotEmptyException;
import model.Room;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CRUD resource for campus rooms.
 * Base path: /api/v1/rooms
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // In-memory store (replace with a service/repository in production)
    private static final Map<Integer, Room> STORE = new ConcurrentHashMap<>();
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);

    static {
        STORE.put(1, new Room(1, "Lecture Hall A", "Main Block", 1, 200));
        STORE.put(2, new Room(2, "Lab 101",        "Science Block", 1, 40));
        ID_SEQ.set(3);
    }

    // GET /rooms
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(STORE.values());
    }

    // GET /rooms/{id}
    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") int id) {
        Room room = STORE.get(id);
        if (room == null) {
            throw new NotFoundException("Room with id " + id + " not found.");
        }
        return room;
    }

    // POST /rooms
    @POST
    public Response createRoom(Room room) {
        int newId = ID_SEQ.getAndIncrement();
        room.setId(newId);
        STORE.put(newId, room);
        URI location = UriBuilder.fromResource(RoomResource.class)
                .path(String.valueOf(newId)).build();
        return Response.created(location).entity(room).build();
    }

    // PUT /rooms/{id}
    @PUT
    @Path("/{id}")
    public Response updateRoom(@PathParam("id") int id, Room updated) {
        if (!STORE.containsKey(id)) {
            throw new NotFoundException("Room with id " + id + " not found.");
        }
        updated.setId(id);
        STORE.put(id, updated);
        return Response.ok(updated).build();
    }

    // DELETE /rooms/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") int id) {
        Room room = STORE.get(id);
        if (room == null) {
            throw new NotFoundException("Room with id " + id + " not found.");
        }
        // Reject deletion if sensors are still linked (checked by SensorResource)
        boolean hasSensors = SensorResource.STORE.values().stream()
                .anyMatch(s -> s.getRoomId() == id);
        if (hasSensors) {
            throw new RoomNotEmptyException(
                    "Room " + id + " still has sensors assigned. Remove them first.");
        }
        STORE.remove(id);
        return Response.noContent().build();
    }
}
