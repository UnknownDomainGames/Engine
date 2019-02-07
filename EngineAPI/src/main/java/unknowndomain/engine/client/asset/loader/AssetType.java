package unknowndomain.engine.client.asset.loader;

import javax.annotation.Nonnull;

public class AssetType<T> {

    private final Class<T> assetClass;
    private final String name;
    private final AssetLoader<T> loader;

    public AssetType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetLoader<T> loader) {
        this.assetClass = assetClass;
        this.name = name;
        this.loader = loader;
    }

    @Nonnull
    public Class<T> getAssetClass() {
        return assetClass;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public AssetLoader<T> getLoader() {
        return loader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetType<?> assetType = (AssetType<?>) o;

        return name.equals(assetType.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "AssetType{" +
                "assetClass=" + assetClass +
                ", name='" + name + '\'' +
                '}';
    }
}
