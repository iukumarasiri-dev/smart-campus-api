package exception;

/**
 * Thrown when an operation references a resource (e.g. a Room) that does not exist.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    public LinkedResourceNotFoundException(String message) {
        super(message);
    }

    public LinkedResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
