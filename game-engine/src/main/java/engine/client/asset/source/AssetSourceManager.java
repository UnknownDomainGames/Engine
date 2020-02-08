package engine.client.asset.source;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface AssetSourceManager {

    Optional<Path> getPath(@Nonnull String url);

    List<Path> getPaths(@Nonnull String url);

    Optional<AssetSource> getSource(@Nonnull String url);

    List<AssetSource> getSources(@Nonnull String url);

    LinkedList<AssetSource> getSources();

    static AssetSourceManager instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<AssetSourceManager> instance = () -> {
            throw new IllegalStateException("AssetSourceManager is not initialized");
        };

        public static void setInstance(AssetSourceManager instance) {
            Internal.instance = () -> instance;
        }
    }
}
