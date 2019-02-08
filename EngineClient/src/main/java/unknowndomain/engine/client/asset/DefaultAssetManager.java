package unknowndomain.engine.client.asset;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.event.asset.AssetReloadEvent;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DefaultAssetManager implements AssetManager {

    private final List<AssetSource> assetSources = new LinkedList<>();

    private final List<Runnable> reloadListener = new LinkedList<>();

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
    public List<AssetSource> getAllSources(AssetPath path) {
        List<AssetSource> result = new LinkedList<>();
        for (AssetSource assetSource : assetSources) {
            if (assetSource.exists(path)) {
                result.add(assetSource);
            }
        }
        return List.copyOf(result);
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

    @Nonnull
    @Override
    public List<Path> getAllPaths(@Nonnull AssetPath path) {
        List<Path> result = new LinkedList<>();
        for (AssetSource assetSource : assetSources) {
            Path _path = assetSource.toPath(path);
            if (Files.exists(_path)) {
                result.add(_path);
            }
        }
        return List.copyOf(result);
    }

    public List<AssetSource> getSources() {
        return assetSources;
    }

    @Override
    public void reload() {
        reloadListener.forEach(Runnable::run);
        if (Platform.getEngine().isPlaying()) {
            Platform.getEngine().getCurrentGame().getEventBus().post(new AssetReloadEvent());
        }
    }

    public List<Runnable> getReloadListener() {
        return reloadListener;
    }
}
