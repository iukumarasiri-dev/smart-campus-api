package exception;

/**
 * Thrown when a sensor is in a state (FAULTY / INACTIVE) that prevents it
 * from serving the requested operation.
 */
public class SensorUnavailableException extends RuntimeException {

    public SensorUnavailableException(String message) {
        super(message);
    }

    public SensorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
