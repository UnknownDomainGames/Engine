package unknowndomain.engine._mod.exception;

public class DependencyParseException extends RuntimeException {

    public DependencyParseException() {
    }

    public DependencyParseException(String message) {
        super(message);
    }

    public DependencyParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyParseException(Throwable cause) {
        super(cause);
    }
}
