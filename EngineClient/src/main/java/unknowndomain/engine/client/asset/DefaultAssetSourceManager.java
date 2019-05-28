package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DefaultAssetSourceManager implements AssetSourceManager {

    private final LinkedList<AssetSource> assetSources = new LinkedList<>();

    @Override
    public Optional<AssetSource> getSource(AssetPath path) {
        for (AssetSource assetSource : assetSources) {
            if (assetSource.exists(path)) {
                return Optional.of(assetSource);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AssetSource> getSources(@Nonnull AssetPath path) {
        List<AssetSource> result = new ArrayList<>();
        for (AssetSource assetSource : assetSources) {
            if (assetSource.exists(path)) {
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
            Path _path = assetSource.toPath(path);
            if (Files.exists(_path)) {
                return Optional.of(_path);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Path> getPaths(@Nonnull AssetPath path) {
        List<Path> result = new ArrayList<>();
        for (AssetSource assetSource : assetSources) {
            Path _path = assetSource.toPath(path);
            if (Files.exists(_path)) {
                result.add(_path);
            }
        }
        return List.copyOf(result);
    }
}
