package nullengine.client.asset.source;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface AssetSourceManager {

    Optional<Path> getPath(@Nonnull String url);

    List<Path> getPaths(@Nonnull String url);

    Optional<AssetSource> getSource(@Nonnull String url);

    List<AssetSource> getSources(@Nonnull String url);

    LinkedList<AssetSource> getSources();
}
