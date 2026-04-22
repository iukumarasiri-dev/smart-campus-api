# Smart Campus Sensor & Room Management API

A RESTful Smart Campus API built with JAX-RS (Jersey) for managing university rooms, sensors, and sensor readings.

- **Author:** Isuru Udara
- **Student ID:** w2120151 / 20240906
- **Module:** 5COSC022W — Client-Server Architectures
- **University:** University of Westminster

---

## Tech Stack

- Java 11
- JAX-RS (Jakarta RESTful Web Services)
- Jersey 3.1.3 (JAX-RS Implementation)
- Grizzly HTTP Server (Embedded)
- Jackson (JSON Serialisation)
- Maven (Build Tool)
- In-memory storage (HashMap / ArrayList)

---

## Project Structure

```
src/main/java/com/smartcampus/
├── Main.java
├── SmartCampusApp.java
├── model/
│   ├── Room.java
│   ├── Sensor.java
│   ├── SensorReading.java
│   └── DataStore.java
├── resource/
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── exception/
│   ├── RoomNotEmptyException.java
│   ├── LinkedResourceNotFoundException.java
│   ├── SensorUnavailableException.java
│   └── mapper/
│       ├── RoomNotEmptyExceptionMapper.java
│       ├── LinkedResourceNotFoundExceptionMapper.java
│       ├── SensorUnavailableExceptionMapper.java
│       └── GlobalExceptionMapper.java
└── filter/
    └── LoggingFilter.java
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/` | API Discovery |
| GET | `/api/v1/rooms` | Get all rooms |
| POST | `/api/v1/rooms` | Create a room |
| GET | `/api/v1/rooms/{roomId}` | Get a room |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room |
| GET | `/api/v1/sensors` | Get all sensors |
| GET | `/api/v1/sensors?type=CO2` | Filter sensors by type |
| POST | `/api/v1/sensors` | Create a sensor |
| DELETE | `/api/v1/sensors/{sensorId}` | Delete a sensor |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a reading |

---

## How to Build & Run

### Prerequisites
- Java JDK 11+
- Apache Maven 3.6+

### Step 1 — Clone the repository
```bash
git clone https://github.com/iukumarasiri-dev/smart-campus-api.git
cd smart-campus-api
```

### Step 2 — Build the project
```bash
mvn clean package
```

### Step 3 — Run the server
```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

### Step 4 — Server is running at
```
http://localhost:8080/api/v1/
```

---

## Sample curl Commands

### 1. Get API Discovery
```bash
curl -X GET http://localhost:8080/api/v1/
```

### 2. Get All Rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 3. Create a New Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "HALL-101", "name": "Main Hall", "capacity": 100}'
```

### 4. Create a New Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-002", "type": "CO2", "status": "ACTIVE", "currentValue": 350.0, "roomId": "LIB-301"}'
```

### 5. Filter Sensors by Type
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 6. Add a Sensor Reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 24.5}'
```

### 7. Delete a Room (with sensors — triggers 409)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

### 8. Create Sensor with Invalid Room (triggers 422)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-999", "type": "CO2", "status": "ACTIVE", "currentValue": 350.0, "roomId": "FAKE-999"}'
```

---

## Coursework Report — Question Answers

### Part 1: Service Architecture & Setup

**Q1: JAX-RS Resource Lifecycle & Data Synchronisation**

By default, JAX-RS creates a new resource class instance per request (PerRequest scope). This means instance fields cannot hold shared state — every request would start fresh. The implementation solves this using the Singleton pattern in `DataStore`: a `static final` instance is shared across all resource instances, ensuring all requests read and write to the same in-memory HashMaps. A known risk is thread-safety — HashMap is not thread-safe under concurrent writes. The production fix would be `ConcurrentHashMap`, but for single-user coursework testing the current approach is acceptable.

---

**Q2: HATEOAS — Hypermedia as a Hallmark of Advanced REST**

HATEOAS (Hypermedia As The Engine Of Application State) means API responses embed links describing available next actions, so clients navigate the API by following links rather than hardcoding URLs. The discovery endpoint (`GET /api/v1/`) already implements this by returning resource paths. Compared to static documentation, HATEOAS means URL changes don't break clients, responses are always accurate, and no external docs are needed. This represents Level 3 of the Richardson Maturity Model — the highest maturity level of RESTful design.

---

### Part 2: Room Management

**Q1: Returning Only IDs vs. Full Room Objects**

Returning full objects (current approach) means the client gets everything in one request but the payload grows with scale. Returning only IDs produces a lighter response but forces the client to make a separate `GET /rooms/{id}` call for each room — the N+1 problem. For 50 rooms that is 51 HTTP requests, significantly increasing latency. Since the Room model has a small, bounded number of fields, returning full objects is the pragmatic and correct choice here.

---

**Q2: Is DELETE Idempotent?**

No — the implementation is not strictly idempotent. The first `DELETE /rooms/LIB-301` returns `200 OK`; the second returns `404 Not Found` because the room no longer exists. The response code changes between calls. RFC 9110 states DELETE should be idempotent in terms of server state (the room stays deleted), but because the response differs, a client retrying after a network timeout might incorrectly assume the first delete failed. To make it fully idempotent, repeated deletes could return `204 No Content` instead of `404`.

---

### Part 3: Sensor Operations & Filtering

**Q1: @Consumes(APPLICATION_JSON) — Format Mismatch Consequences**

If a client sends `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS compares the header against `@Consumes(MediaType.APPLICATION_JSON)` at the routing stage and immediately returns `HTTP 415 Unsupported Media Type` — before the resource method even executes. This protects the DataStore from receiving unparseable data and prevents accidental 500 errors from Jackson failing to deserialise a non-JSON body.

---

**Q2: @QueryParam vs. Path Segment for Filtering**

`GET /api/v1/sensors?type=CO2` is superior to `GET /api/v1/sensors/type/CO2` because query parameters are the REST convention for filtering a collection, not identifying a unique resource. Path segments should identify a specific resource (e.g., `/sensors/TEMP-001`). Query params are naturally optional — omitting `?type=` returns all sensors without a separate route. They also compose easily: `?type=CO2&status=ACTIVE`. A path-based filter implies `type/CO2` is a distinct resource, which violates REST naming semantics.

---

### Part 4: Sub-Resources & Readings

**Q1: Sub-Resource Locator Pattern — Architectural Benefits**

The sub-resource locator delegates `/readings` routing to a dedicated `SensorReadingResource` class instead of expanding `SensorResource` indefinitely. Benefits include: (1) Single Responsibility — each class has one job; (2) the `sensorId` context is injected once via constructor, not re-passed to every method; (3) avoids the "God Controller" problem where one class grows to hundreds of lines; (4) `SensorReadingResource` can be unit tested in isolation without spinning up the full JAX-RS runtime; (5) new sub-resources (e.g., `/alerts`, `/calibrations`) each get their own class — growth is additive, not expansive.

---

### Part 5: Error Handling & Logging

**Q1: HTTP 422 vs. 404 — Semantic Accuracy**

`404 Not Found` means the requested URL does not exist. `422 Unprocessable Entity` means the URL was valid and the JSON was parsed, but the payload content is semantically invalid. When a client posts a valid sensor JSON to `/api/v1/sensors` but references a non-existent `roomId`, the endpoint itself exists and works — only the data inside is wrong. Returning `404` would misleadingly imply the `/sensors` endpoint doesn't exist. `422` precisely communicates a data integrity violation within the payload, not a routing problem.

---

**Q2: Cybersecurity Risks of Exposing Stack Traces**

Exposing raw Java stack traces is classified under OWASP A05 (Security Misconfiguration). An attacker can extract: (1) technology fingerprinting — exact framework and library versions (Jersey, Jackson) to search for known CVEs; (2) internal package and class structure — method names, class names, and line numbers to craft targeted exploits; (3) business logic inference — method names like `deleteRoom` or `getSensorIds` reveal internal workflows. `GlobalExceptionMapper` mitigates this by catching all `Throwable`, logging full detail server-side only via `LOGGER.severe()`, and returning only a safe generic `500` message to the client.

**Q3: JAX-RS Filters vs. Manual Logging**

Manual `Logger.info()` calls in every method create duplicated boilerplate across dozens of methods, are easily forgotten in new endpoints, and never fire for requests rejected before reaching a resource method (e.g., 415 errors). `LoggingFilter` implements `ContainerRequestFilter` and `ContainerResponseFilter`, running automatically at the framework level for every request and response without any resource class knowing it exists. Adding a new endpoint tomorrow automatically logs it — zero extra code. This follows the Separation of Concerns principle: resource classes handle business logic, the filter handles observability independently.

---

## Error Responses

| Scenario | HTTP Status |
|----------|------------|
| Room deleted with active sensors | 409 Conflict |
| Sensor created with invalid roomId | 422 Unprocessable Entity |
| Reading added to MAINTENANCE sensor | 403 Forbidden |
| Resource not found | 404 Not Found |
| Unexpected server error | 500 Internal Server Error |