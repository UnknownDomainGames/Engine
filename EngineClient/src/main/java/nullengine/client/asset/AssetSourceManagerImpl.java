package nullengine.client.asset;

import nullengine.client.asset.source.AssetSource;
import nullengine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AssetSourceManagerImpl implements AssetSourceManager {

    private final LinkedList<AssetSource> assetSources = new LinkedList<>();

    @Override
    public Optional<Path> getPath(@Nonnull String url) {
        for (AssetSource assetSource : assetSources) {
            var path = assetSource.getPath(url);
            if (path.isPresent()) {
                return path;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Path> getPaths(@Nonnull String url) {
        List<Path> result = new ArrayList<>();
        assetSources.forEach(assetSource -> assetSource.getPath(url).ifPresent(result::add));
        return List.copyOf(result);
    }

    @Override
    public Optional<AssetSource> getSource(@Nonnull String url) {
        for (AssetSource assetSource : assetSources) {
            if (assetSource.getPath(url).isPresent()) {
                return Optional.of(assetSource);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AssetSource> getSources(@Nonnull String url) {
        List<AssetSource> result = new ArrayList<>();
        for (AssetSource assetSource : assetSources) {
            if (assetSource.getPath(url).isPresent()) {
                result.add(assetSource);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public LinkedList<AssetSource> getSources() {
        return assetSources;
    }
}
