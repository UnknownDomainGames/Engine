package unknowndomain.engine.client.asset;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.notEmpty;

public class AssetManagerImpl implements AssetManager {

    private final AssetSourceManager sourceManager = new AssetSourceManagerImpl();

    @Override
    public <T extends Asset> AssetType<T> createType(Class<T> assetClass, AssetFactory<T> factory) {
        return createType(assetClass, assetClass.getSimpleName(), factory);
    }

    @Override
    public <T extends Asset> AssetType<T> createType(Class<T> assetClass, String name, AssetFactory<T> factory) {
        requireNonNull(assetClass);
        notEmpty(name);
        requireNonNull(factory);

        factory.init(getSourceManager());
        return new AssetType<>(assetClass, name, factory);
    }

    @Override
    public AssetSourceManager getSourceManager() {
        return sourceManager;
    }
}
