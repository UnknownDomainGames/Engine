package unknowndomain.engine.mod.exception;

import java.nio.file.Path;

public class InvalidModDescriptorException extends RuntimeException {
    public InvalidModDescriptorException(Path path, Throwable cause) {
        super(String.format("Invalid mod descriptor. Source: %s", path.toAbsolutePath()), cause);
    }

    public InvalidModDescriptorException(Path path) {
        super(String.format("Invalid mod descriptor. Source: %s", path.toAbsolutePath()));
    }

    public InvalidModDescriptorException(String message) {
        super(message);
    }
}
