package com.smartcampus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    // Single shared instance (Singleton)
    private static final DataStore instance = new DataStore();

    // In-memory storage
    public Map<String, Room> rooms = new HashMap<>();
    public Map<String, Sensor> sensors = new HashMap<>();
    public Map<String, List<SensorReading>> readings = new HashMap<>();

    // Private constructor
    private DataStore() {
        // Add some sample data
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LAB-101");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        // Link sensors to rooms
        r1.getSensorIds().add(s1.getId());
        r2.getSensorIds().add(s2.getId());

        // Empty reading lists
        readings.put(s1.getId(), new ArrayList<>());
        readings.put(s2.getId(), new ArrayList<>());
    }

    // Get the single instance
    public static DataStore getInstance() {
        return instance;
    }
}
