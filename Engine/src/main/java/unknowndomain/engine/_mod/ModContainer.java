package unknowndomain.engine._mod;

import org.slf4j.Logger;
import unknowndomain.engine.mod.ModMetadata;

import java.nio.file.Path;

/**
 * The runtime mod container of the mod.
 */
public interface ModContainer {

    String getModId();

    /**
     * The instance of the class which is annotated by {@link Mod}
     *
     * @return The instance
     * @see Mod
     */
    Object getInstance();

    ClassLoader getClassLoader();

    Path getSource();

    Logger getLogger();

    ModMetadata getMetadata();
}
