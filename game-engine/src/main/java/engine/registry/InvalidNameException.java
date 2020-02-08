package engine.registry;

public final class InvalidNameException extends RuntimeException {

    public InvalidNameException(String name) {
        super("Name " + name + "is invalid");
    }
}
