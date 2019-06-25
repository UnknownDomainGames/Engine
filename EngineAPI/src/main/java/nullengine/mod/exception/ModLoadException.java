package nullengine.mod.exception;

public class ModLoadException extends RuntimeException {

    public ModLoadException(String modid) {
        super(String.format("Cannot load mod \"%s\" because do not support load it.", modid));
    }

    public ModLoadException(String modid, Throwable cause) {
        super(String.format("Cannot load mod \"%s\" because caught a exception.", modid), cause);
    }
}
