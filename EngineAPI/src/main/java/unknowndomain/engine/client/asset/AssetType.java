package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;

public class AssetType<T extends Asset> {

    private final Class<T> assetClass;
    private final String name;
    private final AssetFactory<T> factory;

    AssetType(@Nonnull Class<T> assetClass, @Nonnull String name, AssetFactory<T> factory) {
        this.assetClass = assetClass;
        this.name = name;
        this.factory = factory;
    }

    public Class<T> getAssetClass() {
        return assetClass;
    }

    public String getName() {
        return name;
    }

    public AssetFactory<T> getFactory() {
        return factory;
    }

    public T getAsset(AssetPath path) {
        return factory.getAsset(this, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetType<?> assetType = (AssetType<?>) o;

        if (!assetClass.equals(assetType.assetClass)) return false;
        if (!name.equals(assetType.name)) return false;
        return factory.equals(assetType.factory);
    }

    @Override
    public int hashCode() {
        int result = assetClass.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + factory.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AssetType{" +
                "assetClass=" + assetClass +
                ", name='" + name + '\'' +
                ", factory=" + factory +
                '}';
    }
}
