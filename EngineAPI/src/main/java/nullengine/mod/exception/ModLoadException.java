package nullengine.mod.exception;

public class ModLoadException extends RuntimeException {

    public ModLoadException(String message) {
        super(message);
    }

    public ModLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
