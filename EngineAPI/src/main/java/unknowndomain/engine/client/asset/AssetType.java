package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetType<T extends Asset> {

    private final Class<T> assetClass;
    private final String name;
    private final String fileNameExtension;
    private final AssetFactory<T> factory;

    AssetType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nullable String fileNameExtension, @Nonnull AssetFactory<T> factory) {
        this.assetClass = assetClass;
        this.name = name;
        this.fileNameExtension = fileNameExtension;
        this.factory = factory;
    }

    @Nonnull
    public Class<T> getAssetClass() {
        return assetClass;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public String getFileNameExtension() {
        return fileNameExtension;
    }

    @Nonnull
    public AssetFactory<T> getFactory() {
        return factory;
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
