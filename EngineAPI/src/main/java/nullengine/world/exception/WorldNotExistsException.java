package nullengine.world.exception;

public class WorldNotExistsException extends RuntimeException {
    public WorldNotExistsException(String world) {
        super(String.format("World \"%s\" not exists", world));
    }
}
