package exception;

/**
 * Thrown when a room cannot be deleted because it still has sensors assigned.
 */
public class RoomNotEmptyException extends RuntimeException {

    public RoomNotEmptyException(String message) {
        super(message);
    }

    public RoomNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
