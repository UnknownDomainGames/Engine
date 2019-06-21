package unknowndomain.engine.mod;

import org.slf4j.Logger;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.mod.annotation.Mod;
import unknowndomain.engine.util.versioning.Version;

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
