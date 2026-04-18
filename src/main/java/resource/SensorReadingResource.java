package resource;

import model.SensorReading;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Sub-resource for sensor readings.
 * Reachable via /api/v1/sensors/{sensorId}/readings
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // Shared in-memory store across all sensor instances
    static final Map<Integer, SensorReading> STORE = new ConcurrentHashMap<>();
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);

    static {
        STORE.put(1, new SensorReading(1, 1, 22.5, "°C",  Instant.parse("2026-04-18T08:00:00Z")));
        STORE.put(2, new SensorReading(2, 1, 45.0, "%",   Instant.parse("2026-04-18T08:00:00Z")));
        STORE.put(3, new SensorReading(3, 2, 21.8, "°C",  Instant.parse("2026-04-18T08:05:00Z")));
        ID_SEQ.set(4);
    }

    private final int sensorId;

    public SensorReadingResource(int sensorId) {
        this.sensorId = sensorId;
    }

    // GET /sensors/{sensorId}/readings
    @GET
    public List<SensorReading> getReadings() {
        return STORE.values().stream()
                .filter(r -> r.getSensorId() == sensorId)
                .collect(Collectors.toList());
    }

    // GET /sensors/{sensorId}/readings/{id}
    @GET
    @Path("/{id}")
    public SensorReading getReading(@PathParam("id") int id) {
        SensorReading reading = STORE.get(id);
        if (reading == null || reading.getSensorId() != sensorId) {
            throw new NotFoundException(
                    "Reading " + id + " not found for sensor " + sensorId + ".");
        }
        return reading;
    }

    // POST /sensors/{sensorId}/readings
    @POST
    public Response addReading(SensorReading reading) {
        int newId = ID_SEQ.getAndIncrement();
        reading.setId(newId);
        reading.setSensorId(sensorId);
        if (reading.getTimestamp() == null) {
            reading.setTimestamp(Instant.now());
        }
        STORE.put(newId, reading);
        URI location = UriBuilder.fromResource(SensorResource.class)
                .path(String.valueOf(sensorId))
                .path("readings")
                .path(String.valueOf(newId))
                .build();
        return Response.created(location).entity(reading).build();
    }

    // DELETE /sensors/{sensorId}/readings/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteReading(@PathParam("id") int id) {
        SensorReading reading = STORE.get(id);
        if (reading == null || reading.getSensorId() != sensorId) {
            throw new NotFoundException(
                    "Reading " + id + " not found for sensor " + sensorId + ".");
        }
        STORE.remove(id);
        return Response.noContent().build();
    }
}
