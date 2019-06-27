package nullengine.mod;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface ModLoader {

    ModContainer load(Collection<Path> sources, ModMetadata metadata, List<ModContainer> dependencies);
}
