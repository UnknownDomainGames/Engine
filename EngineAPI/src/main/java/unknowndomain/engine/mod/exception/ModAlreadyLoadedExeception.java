package unknowndomain.engine.mod.exception;

public class ModAlreadyLoadedExeception extends RuntimeException {

    public ModAlreadyLoadedExeception(String modid) {
        super(String.format("Mod \"%s\" already loaded.", modid));
    }
}
