package nullengine.exception;

public class NotInitializationException extends RuntimeException {
    public NotInitializationException() {
    }

    public NotInitializationException(String message) {
        super(message);
    }

    public NotInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInitializationException(Throwable cause) {
        super(cause);
    }
}
