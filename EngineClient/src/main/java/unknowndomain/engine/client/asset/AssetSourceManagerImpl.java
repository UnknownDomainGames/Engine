package unknowndomain.engine.client.asset;

import com.github.mouse0w0.lib4j.observable.collection.ObservableCollections;
import com.github.mouse0w0.lib4j.observable.collection.ObservableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AssetSourceManagerImpl implements AssetSourceManager {

    private final ObservableList<AssetSource> assetSources = ObservableCollections.observableList(new LinkedList<>());

    private final List<Consumer<AssetSourceManager>> onChangeListeners = new LinkedList<>();

    public AssetSourceManagerImpl() {
        assetSources.addChangeListener(change -> notifyChange());
    }

    @Override
    public Optional<AssetSource> getSource(String path) {
        for (AssetSource assetSource : assetSources) {
            if (assetSource.has(path)) {
                return Optional.of(assetSource);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AssetSource> getSources(String path) {
        List<AssetSource> result = new LinkedList<>();
        for (AssetSource assetSource : assetSources) {
            if (assetSource.has(path)) {
                result.add(assetSource);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public List<AssetSource> getSources() {
        return assetSources;
    }

    protected void notifyChange() {
        for (Consumer<AssetSourceManager> consumer : onChangeListeners) {
            consumer.accept(this);
        }
    }

    @Override
    public void addChangeListener(Consumer<AssetSourceManager> changeListener) {
        onChangeListeners.add(changeListener);
    }

    @Override
    public void removeChangeListener(Consumer<AssetSourceManager> changeListener) {
        onChangeListeners.remove(changeListener);
    }
}
