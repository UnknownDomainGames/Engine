package nullengine.mod.exception;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Collection;

public class InvalidModMetadataException extends RuntimeException {
    public InvalidModMetadataException(Collection<Path> sources, Throwable cause) {
        super(String.format("Invalid mod metadata. Sources: [%s]", StringUtils.join(sources, ",")), cause);
    }

    public InvalidModMetadataException(Collection<Path> sources) {
        super(String.format("Cannot find metadata. Sources: [%s]", StringUtils.join(sources, ",")));
    }

    public InvalidModMetadataException(String message) {
        super(message);
    }
}
