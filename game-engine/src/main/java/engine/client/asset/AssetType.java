package engine.client.asset;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;

public final class AssetType<T> {

    private final Class<T> type;
    private final String name;
    private final String parentLocation;
    private final String extensionName;
    private final AssetProvider<T> provider;

    private AssetType(Builder<T> builder) {
        this.type = builder.type;
        this.name = Strings.isNullOrEmpty(builder.name) ? type.getSimpleName() : builder.name;
        this.parentLocation = builder.parentLocation;
        this.extensionName = builder.extensionName;
        this.provider = builder.provider;
    }

    @Nonnull
    public Class<T> getType() {
        return type;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getParentLocation() {
        return parentLocation;
    }

    @Nonnull
    public String getExtensionName() {
        return extensionName;
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

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }

    public static final class Builder<T> {
        private final Class<T> type;
        private String name;
        private String parentLocation;
        private String extensionName;
        private AssetProvider<T> provider;

        private Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> parentLocation(String parentLocation) {
            this.parentLocation = parentLocation;
            return this;
        }

        public Builder<T> extensionName(String extensionName) {
            this.extensionName = extensionName;
            return this;
        }

        public Builder<T> provider(AssetProvider<T> provider) {
            this.provider = provider;
            return this;
        }

        public AssetType<T> build() {
            return new AssetType<>(this);
        }
    }
}
