package unknowndomain.engine.mod.exception;

import java.nio.file.Path;

public class InvalidModException extends RuntimeException {

    public InvalidModException(Path path) {
        super(String.format("Cannot find mod descriptor. Source: %s", path.toAbsolutePath()));
    }
}
