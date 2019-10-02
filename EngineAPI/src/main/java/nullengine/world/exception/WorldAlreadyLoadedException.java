package nullengine.world.exception;

public final class WorldAlreadyLoadedException extends WorldLoadException {

    public WorldAlreadyLoadedException(String world) {
        super(String.format("World \"%s\" already exists", world));
    }
}
