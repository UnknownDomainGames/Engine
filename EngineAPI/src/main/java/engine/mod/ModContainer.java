package engine.mod;

import engine.event.EventBus;
import engine.mod.annotation.Mod;
import engine.util.versioning.Version;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collection;

/**
 * The runtime mod container of the mod.
 */
public interface ModContainer {

    String getId();

    Version getVersion();

    /**
     * The instance of the class which is annotated by {@link Mod}
     *
     * @return The instance
     * @see Mod
     */
    Object getInstance();

    ClassLoader getClassLoader();

    Collection<Path> getSources();

    Path getConfigPath();

    Path getDataPath();

    ModAssets getAssets();

    EventBus getEventBus();

    Logger getLogger();

    ModMetadata getMetadata();
}
