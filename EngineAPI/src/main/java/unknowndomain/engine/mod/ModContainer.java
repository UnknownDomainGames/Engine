package unknowndomain.engine.mod;

import org.slf4j.Logger;

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

    Logger getLogger();

    ModMetadata getMetadata();
}
