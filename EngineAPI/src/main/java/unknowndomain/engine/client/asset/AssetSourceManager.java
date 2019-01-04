package unknowndomain.engine.client.asset;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface AssetSourceManager {

    Optional<AssetSource> getSource(String path);

    List<AssetSource> getSources(String path);

    List<AssetSource> getSources();

    void notifyChange();

    void addChangeListener(Consumer<AssetSourceManager> changeListener);

    void removeChangeListener(Consumer<AssetSourceManager> changeListener);
}
