package nullengine.mod;

import nullengine.mod.exception.InvalidModMetadataException;

import java.nio.file.Path;
import java.util.Collection;

public interface ModMetadataFinder {

    ModMetadata find(Collection<Path> sources) throws InvalidModMetadataException;
}
