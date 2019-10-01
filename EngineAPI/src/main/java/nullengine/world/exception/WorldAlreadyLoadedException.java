package nullengine.world.exception;

public final class WorldAlreadyLoadedException extends RuntimeException {

    public WorldAlreadyLoadedException(String world) {
        super(String.format("World \"%s\" already exists", world));
    }
}
