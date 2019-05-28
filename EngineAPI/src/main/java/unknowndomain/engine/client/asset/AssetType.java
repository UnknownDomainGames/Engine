package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;

public class AssetType<T> {

    private final Class<T> type;
    private final String name;
    private final AssetProvider<T> provider;

    AssetType(@Nonnull Class<T> type, @Nonnull String name, @Nonnull AssetProvider<T> provider) {
        this.type = type;
        this.name = name;
        this.provider = provider;
    }

    @Nonnull
    public Class<T> getType() {
        return type;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    AssetProvider<T> getProvider() {
        return provider;
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
                "type=" + type.getName() +
                ", name='" + name + '\'' +
                '}';
    }
}
