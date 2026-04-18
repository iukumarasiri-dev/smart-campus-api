package model;

import java.util.Objects;

/**
 * Represents an IoT sensor installed in a campus room.
 */
public class Sensor {

    public enum Status {
        ACTIVE, INACTIVE, FAULTY
    }

    private int id;
    private int roomId;
    private String type;       // e.g. "TEMPERATURE", "HUMIDITY", "CO2", "MOTION"
    private Status status;

    public Sensor() {}

    public Sensor(int id, int roomId, String type, Status status) {
        this.id = id;
        this.roomId = roomId;
        this.type = type;
        this.status = status;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensor)) return false;
        Sensor sensor = (Sensor) o;
        return id == sensor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sensor{id=" + id + ", roomId=" + roomId + ", type='" + type
                + "', status=" + status + "}";
    }
}
