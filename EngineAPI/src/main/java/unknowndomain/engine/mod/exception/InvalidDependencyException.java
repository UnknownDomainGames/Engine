package unknowndomain.engine.mod.exception;

public class InvalidDependencyException extends RuntimeException {

    public InvalidDependencyException(String message) {
        super(message);
    }

    public InvalidDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
