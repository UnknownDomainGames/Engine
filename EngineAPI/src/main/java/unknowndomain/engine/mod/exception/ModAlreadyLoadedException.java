package unknowndomain.engine.mod.exception;

public class ModAlreadyLoadedException extends RuntimeException {

    public ModAlreadyLoadedException(String modid) {
        super(String.format("Mod \"%s\" already loaded.", modid));
    }
}
