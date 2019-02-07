package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.source.AssetSource;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface AssetManager {

    Optional<AssetSource> getSource(@Nonnull AssetPath path);

    @Nonnull
    List<AssetSource> getAllSources(@Nonnull AssetPath path);

    Optional<Path> getPath(@Nonnull AssetPath path);

    @Nonnull
    List<Path> getAllPaths(@Nonnull AssetPath path);

    void reload();
}
