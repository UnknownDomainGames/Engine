package unknowndomain.engine.mod;

import unknowndomain.engine.mod.exception.InvalidModMetadataException;

import java.nio.file.Path;
import java.util.Collection;

public interface ModMetadataFinder {

    ModMetadata find(Collection<Path> sources) throws InvalidModMetadataException;
}
