package nullengine.world.exception;

public final class WorldAlreadyExistsException extends RuntimeException {

    public WorldAlreadyExistsException(String world) {
        super(String.format("World \"%s\" already exists", world));
    }
}
