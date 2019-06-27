package nullengine.exception;

public class NoInitializationException extends RuntimeException {
    public NoInitializationException() {
    }

    public NoInitializationException(String message) {
        super(message);
    }

    public NoInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInitializationException(Throwable cause) {
        super(cause);
    }
}
