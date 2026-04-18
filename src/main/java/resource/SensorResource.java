package resource;

import exception.LinkedResourceNotFoundException;
import exception.SensorUnavailableException;
import model.Sensor;

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
 * CRUD resource for sensors.
 * Base path: /api/v1/sensors
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // Package-visible so RoomResource can check for linked sensors
    static final Map<Integer, Sensor> STORE = new ConcurrentHashMap<>();
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);

    static {
        STORE.put(1, new Sensor(1, 1, "TEMPERATURE", Sensor.Status.ACTIVE));
        STORE.put(2, new Sensor(2, 1, "HUMIDITY",    Sensor.Status.ACTIVE));
        STORE.put(3, new Sensor(3, 2, "CO2",         Sensor.Status.INACTIVE));
        ID_SEQ.set(4);
    }

    // GET /sensors
    @GET
    public List<Sensor> getAllSensors() {
        return new ArrayList<>(STORE.values());
    }

    // GET /sensors/{id}
    @GET
    @Path("/{id}")
    public Sensor getSensor(@PathParam("id") int id) {
        Sensor sensor = STORE.get(id);
        if (sensor == null) {
            throw new NotFoundException("Sensor with id " + id + " not found.");
        }
        return sensor;
    }

    // POST /sensors
    @POST
    public Response createSensor(Sensor sensor) {
        // Validate that the referenced room exists
        if (!RoomResource.STORE.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "Room with id " + sensor.getRoomId() + " does not exist.");
        }
        int newId = ID_SEQ.getAndIncrement();
        sensor.setId(newId);
        STORE.put(newId, sensor);
        URI location = UriBuilder.fromResource(SensorResource.class)
                .path(String.valueOf(newId)).build();
        return Response.created(location).entity(sensor).build();
    }

    // PUT /sensors/{id}
    @PUT
    @Path("/{id}")
    public Response updateSensor(@PathParam("id") int id, Sensor updated) {
        if (!STORE.containsKey(id)) {
            throw new NotFoundException("Sensor with id " + id + " not found.");
        }
        if (!RoomResource.STORE.containsKey(updated.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "Room with id " + updated.getRoomId() + " does not exist.");
        }
        updated.setId(id);
        STORE.put(id, updated);
        return Response.ok(updated).build();
    }

    // DELETE /sensors/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteSensor(@PathParam("id") int id) {
        if (!STORE.containsKey(id)) {
            throw new NotFoundException("Sensor with id " + id + " not found.");
        }
        STORE.remove(id);
        return Response.noContent().build();
    }

    // Sub-resource locator: /sensors/{id}/readings
    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") int id) {
        Sensor sensor = STORE.get(id);
        if (sensor == null) {
            throw new NotFoundException("Sensor with id " + id + " not found.");
        }
        if (sensor.getStatus() == Sensor.Status.FAULTY) {
            throw new SensorUnavailableException(
                    "Sensor " + id + " is faulty and cannot provide readings.");
        }
        return new SensorReadingResource(id);
    }
}
