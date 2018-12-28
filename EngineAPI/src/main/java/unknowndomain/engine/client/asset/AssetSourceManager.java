package unknowndomain.engine.client.asset;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface AssetSourceManager {

    Optional<AssetSource> getAssetSource(AssetPath path);

    List<AssetSource> getAssetSources(AssetPath path);

    List<AssetSource> getAssetSources();

    void notifyChangeEvent();

    void addChangeListener(Consumer<AssetSourceManager> changeListener);

    void removeChangeListener(Consumer<AssetSourceManager> changeListener);
}
