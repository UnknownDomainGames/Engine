package unknowndomain.engine.mod;

import unknowndomain.engine.mod.exception.InvalidModDescriptorException;

import java.nio.file.Path;

public interface ModMetadataFinder {

    ModMetadata find(Path path) throws InvalidModDescriptorException;
}
