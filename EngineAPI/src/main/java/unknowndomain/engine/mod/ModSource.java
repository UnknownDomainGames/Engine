package unknowndomain.engine.mod;

import java.nio.file.Path;
import java.util.List;

public interface ModSource {

    List<ModMetadata> getLoadableMods();
    
    Path getModPath(ModIdentity modId);

    void refresh();
}
