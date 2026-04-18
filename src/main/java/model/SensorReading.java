package model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a single timestamped measurement produced by a {@link Sensor}.
 */
public class SensorReading {

    private int id;
    private int sensorId;
    private double value;
    private String unit;       // e.g. "°C", "%", "ppm"
    private Instant timestamp;

    public SensorReading() {}

    public SensorReading(int id, int sensorId, double value, String unit, Instant timestamp) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorReading)) return false;
        SensorReading that = (SensorReading) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SensorReading{id=" + id + ", sensorId=" + sensorId + ", value=" + value
                + ", unit='" + unit + "', timestamp=" + timestamp + "}";
    }
}
