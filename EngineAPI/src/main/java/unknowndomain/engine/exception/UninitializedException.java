package unknowndomain.engine.exception;

public class UninitializedException extends RuntimeException {
    public UninitializedException() {
    }

    public UninitializedException(String message) {
        super(message);
    }

    public UninitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UninitializedException(Throwable cause) {
        super(cause);
    }
}
