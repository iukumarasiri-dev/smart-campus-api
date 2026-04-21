package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.DataStore;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final DataStore store = DataStore.getInstance();
    private final String sensorId;

    // Constructor receives sensorId from parent SensorResource
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // ─────────────────────────────────────────
    // GET /api/v1/sensors/{sensorId}/readings
    // Get all readings for a specific sensor
    // ─────────────────────────────────────────
    @GET
    public Response getAllReadings() {

        // Check sensor exists
        Sensor sensor = store.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        // Get readings for this sensor
        List<SensorReading> readings = store.readings.getOrDefault(
            sensorId, new ArrayList<>()
        );

        return Response.ok(readings).build();
    }

    // ─────────────────────────────────────────
    // POST /api/v1/sensors/{sensorId}/readings
    // Add a new reading for a specific sensor
    // ─────────────────────────────────────────
    @POST
    public Response addReading(SensorReading reading) {

        // Check sensor exists
        Sensor sensor = store.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        // ⚠️ Block if sensor is in MAINTENANCE status
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently under MAINTENANCE " +
                "and cannot accept new readings."
            );
        }

        // ⚠️ Block if sensor is OFFLINE
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is OFFLINE " +
                "and cannot accept new readings."
            );
        }

        // Create new reading with auto ID and timestamp
        SensorReading newReading = new SensorReading(reading.getValue());

        // Save reading to list
        store.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(newReading);

        // ⚠️ Side Effect — Update sensor's currentValue
        sensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(newReading)
                .build();
    }

    // ─────────────────────────────────────────
    // GET /api/v1/sensors/{sensorId}/readings/{readingId}
    // Get a specific reading by ID
    // ─────────────────────────────────────────
    @GET
    @Path("/{readingId}")
    public Response getReadingById(@PathParam("readingId") String readingId) {

        // Check sensor exists
        Sensor sensor = store.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        // Find the specific reading
        List<SensorReading> readings = store.readings.getOrDefault(
            sensorId, new ArrayList<>()
        );

        for (SensorReading r : readings) {
            if (r.getId().equals(readingId)) {
                return Response.ok(r).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Reading not found with ID: " + readingId + "\"}")
                .build();
    }
}