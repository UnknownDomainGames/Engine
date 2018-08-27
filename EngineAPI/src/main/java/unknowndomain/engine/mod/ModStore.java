package unknowndomain.engine.mod;

import unknowndomain.engine.Engine;
import unknowndomain.engine.util.Owner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * The mod store responses to grab mod from disk and load into runtime instance.
 * <p>
 * Since we have already decide all the mod will be downloaded to local.
 * </p>
 * <p>
 * The loader could confirm that it can only load from disk
 * </p>
 * <p>
 * Otherwise, we will have a much pure api: (InputStream)->ModContainer, and
 * won't have contains method
 * </p>
 */
@Owner(Engine.class)
public interface ModStore {
    /**
     * Is this mod exist on disk?
     */
    boolean exists(@Nonnull ModIdentifier identifier);

    Path path(@Nonnull ModIdentifier identifier);

    /**
     * Dump the mod data source into local.
     *
     * @param identifier The mod identifier
     * @param stream     The mod data stream
     */
    void store(@Nonnull ModIdentifier identifier, InputStream stream);
}
