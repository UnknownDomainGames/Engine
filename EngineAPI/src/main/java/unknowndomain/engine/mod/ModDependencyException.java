package unknowndomain.engine.mod;

public class ModDependencyException extends RuntimeException {

    public ModDependencyException() {
    }

    public ModDependencyException(String message) {
        super(message);
    }

    public ModDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModDependencyException(Throwable cause) {
        super(cause);
    }
}
