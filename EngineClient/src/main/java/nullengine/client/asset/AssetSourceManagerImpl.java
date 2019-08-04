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
    public Optional<AssetSource> getSource(AssetPath path) {
        for (AssetSource assetSource : assetSources) {
            if (assetSource.toPath(path).isPresent()) {
                return Optional.of(assetSource);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AssetSource> getSources(@Nonnull AssetPath path) {
        List<AssetSource> result = new ArrayList<>();
        for (AssetSource assetSource : assetSources) {
            if (assetSource.toPath(path).isPresent()) {
                result.add(assetSource);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public LinkedList<AssetSource> getSources() {
        return assetSources;
    }

    @Override
    public Optional<Path> getPath(@Nonnull AssetPath path) {
        for (AssetSource assetSource : assetSources) {
            Optional<Path> _path = assetSource.toPath(path);
            if (_path.isPresent()) {
                return _path;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Path> getPaths(@Nonnull AssetPath path) {
        List<Path> result = new ArrayList<>();
        assetSources.forEach(assetSource -> assetSource.toPath(path).ifPresent(result::add));
        return List.copyOf(result);
    }
}
