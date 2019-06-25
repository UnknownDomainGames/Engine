package nullengine.mod;

import nullengine.event.EventBus;
import nullengine.mod.annotation.Mod;
import nullengine.util.versioning.Version;
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

    ModAssets getAssets();

    EventBus getEventBus();

    Logger getLogger();

    ModMetadata getMetadata();
}
