package unknowndomain.engine.client.asset;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface AssetSourceManager {

    Optional<AssetSource> getSource(AssetPath path);

    List<AssetSource> getSources(AssetPath path);

    List<AssetSource> getSources();

    void notifyChangeEvent();

    void addChangeListener(Consumer<AssetSourceManager> changeListener);

    void removeChangeListener(Consumer<AssetSourceManager> changeListener);
}
