package unknowndomain.engine.mod;

import java.nio.file.Path;
import java.util.Collection;

public interface ModLoader {

    ModContainer load(Collection<Path> sources, ModMetadata metadata);
}
