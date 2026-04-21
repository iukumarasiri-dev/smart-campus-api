package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.DataStore;
import com.smartcampus.model.Sensor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    // ─────────────────────────────────────────
    // GET /api/v1/sensors → Get all sensors
    // Optional: ?type=CO2 to filter by type
    // ─────────────────────────────────────────
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(store.sensors.values());

        // If type query param is provided, filter the list
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor s : allSensors) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filtered.add(s);
                }
            }
            return Response.ok(filtered).build();
        }

        return Response.ok(allSensors).build();
    }

    // ─────────────────────────────────────────
    // GET /api/v1/sensors/{sensorId} → Get one sensor
    // ─────────────────────────────────────────
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    // ─────────────────────────────────────────
    // POST /api/v1/sensors → Create a new sensor
    // ─────────────────────────────────────────
    @POST
    public Response createSensor(Sensor sensor) {

        // Check required fields
        if (sensor.getId() == null || sensor.getType() == null || sensor.getRoomId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor ID, Type and Room ID are required\"}")
                    .build();
        }

        // Check if sensor ID already exists
        if (store.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Sensor with this ID already exists\"}")
                    .build();
        }

        // ⚠️ Validate that the roomId actually exists
        if (!store.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Room with ID '" + sensor.getRoomId() + "' does not exist. " +
                "Please create the room first before assigning sensors to it."
            );
        }

        // Set default status if not provided
        if (sensor.getStatus() == null || sensor.getStatus().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // Save sensor
        store.sensors.put(sensor.getId(), sensor);

        // ⚠️ Link sensor to room
        store.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        // Create empty readings list for this sensor
        store.readings.put(sensor.getId(), new ArrayList<>());

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    // ─────────────────────────────────────────
    // DELETE /api/v1/sensors/{sensorId} → Delete sensor
    // ─────────────────────────────────────────
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        // Remove sensor from its room's sensorIds list
        String roomId = sensor.getRoomId();
        if (roomId != null && store.rooms.containsKey(roomId)) {
            store.rooms.get(roomId).getSensorIds().remove(sensorId);
        }

        // Remove sensor readings
        store.readings.remove(sensorId);

        // Remove sensor
        store.sensors.remove(sensorId);

        return Response.ok()
                .entity("{\"message\": \"Sensor " + sensorId + " deleted successfully\"}")
                .build();
    }

    // ─────────────────────────────────────────
    // Sub-resource locator for readings
    // /api/v1/sensors/{sensorId}/readings
    // ─────────────────────────────────────────
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}