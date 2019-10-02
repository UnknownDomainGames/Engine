package nullengine.world.exception;

public class WorldLoadException extends RuntimeException {

    public WorldLoadException(String message) {
        super(message);
    }

    public WorldLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
