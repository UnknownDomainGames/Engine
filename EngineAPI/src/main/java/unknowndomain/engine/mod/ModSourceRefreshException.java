package unknowndomain.engine.mod;

public class ModSourceRefreshException extends RuntimeException {
    public ModSourceRefreshException() {
    }

    public ModSourceRefreshException(String message) {
        super(message);
    }

    public ModSourceRefreshException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModSourceRefreshException(Throwable cause) {
        super(cause);
    }
}
