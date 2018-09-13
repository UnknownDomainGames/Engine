package unknowndomain.engine.mod;

public class ModLoadException extends RuntimeException {

    public ModLoadException() {
    }

    public ModLoadException(String message) {
        super(message);
    }

    public ModLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModLoadException(Throwable cause) {
        super(cause);
    }
}
