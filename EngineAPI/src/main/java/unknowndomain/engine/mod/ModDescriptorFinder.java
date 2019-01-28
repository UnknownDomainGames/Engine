package unknowndomain.engine.mod;

import java.nio.file.Path;

public interface ModDescriptorFinder {

    ModDescriptor find(Path path);
}
