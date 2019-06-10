package unknowndomain.engine.mod;

import unknowndomain.engine.mod.exception.InvalidModDescriptorException;

import java.nio.file.Path;

public interface ModDescriptorFinder {

    ModDescriptor find(Path path) throws InvalidModDescriptorException;
}
