package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.asset.source.AssetSourceManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DefaultAssetSourceManager implements AssetSourceManager {

    private final List<AssetSource> assetSources = new LinkedList<>();

    @Override
    public Optional<AssetSource> getSource(AssetPath path) {
        for (AssetSource assetSource : assetSources) {
            if (assetSource.has(path)) {
                return Optional.of(assetSource);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AssetSource> getSources(AssetPath path) {
        List<AssetSource> result = new LinkedList<>();
        for (AssetSource assetSource : assetSources) {
            if (assetSource.has(path)) {
                result.add(assetSource);
            }
        }
        return List.copyOf(result);
    }

    public List<AssetSource> getSources() {
        return assetSources;
    }
}
