package unknowndomain.engine.mod;

import org.slf4j.Logger;
import unknowndomain.engine.util.Owner;

import java.nio.file.Path;

@Owner(ModManager.class)
public interface ModContainer {
    String getModId();

    Object getInstance();

    Path getSource();

    Logger getLogger();

    ModMetadata getMetadata();

    Path getDataDir();
}
