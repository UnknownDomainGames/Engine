package nullengine.client.asset.source;

import nullengine.client.asset.AssetPath;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface AssetSourceManager {

    Optional<Path> getPath(@Nonnull AssetPath path);

    List<Path> getPaths(@Nonnull AssetPath path);

    Optional<AssetSource> getSource(@Nonnull AssetPath path);

    List<AssetSource> getSources(@Nonnull AssetPath path);

    LinkedList<AssetSource> getSources();
}
