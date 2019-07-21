package nullengine.world.exception;

public final class WorldProviderNotFoundException extends RuntimeException {

    public WorldProviderNotFoundException(String provider) {
        super(String.format("World provider \"%s\" is not found", provider));
    }
}
