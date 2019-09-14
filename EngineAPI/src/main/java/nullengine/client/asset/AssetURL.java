package nullengine.client.asset;

import com.google.common.base.Strings;
import nullengine.registry.Namespaces;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

public final class AssetURL {

    public static final char SEPARATOR = '/';

    private final String domain;
    private final String location;

    public static AssetURL of(@Nonnull String location) {
        return of(Namespaces.getNamespace(), location);
    }

    public static AssetURL of(@Nonnull AssetURL parent, @Nonnull String location) {
        return of(parent.getDomain(), location);
    }

    public static AssetURL of(@Nonnull String domain, @Nonnull String location) {
        return new AssetURL(domain, location);
    }

    public static AssetURL fromString(@Nonnull AssetURL parent, @Nonnull String url) {
        String[] split = url.split(":");
        if (split.length == 1) {
            return of(parent.getDomain(), url);
        } else if (split.length == 2) {
            return of(split[0], split[1]);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static AssetURL fromString(@Nonnull String url) {
        String[] split = url.split(":");
        if (split.length == 1) {
            return of(Namespaces.getNamespace(), url);
        } else if (split.length == 2) {
            return of(split[0], split[1]);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private AssetURL(String domain, String location) {
        if (Strings.isNullOrEmpty(domain)) {
            throw new IllegalArgumentException("Domain cannot be null or empty. Location: " + location);
        }
        this.domain = domain;
        this.location = Validate.notEmpty(location);
    }

    public String getDomain() {
        return domain;
    }

    public String getLocation() {
        return location;
    }

    public String toFileLocation() {
        return domain + SEPARATOR + location;
    }

    public String toFileLocation(AssetType<?> type) {
        return domain + SEPARATOR + type.getParentLocation() + SEPARATOR + location + type.getExtensionName();
    }

    public String toFileLocation(String parentLocation) {
        return domain + SEPARATOR + parentLocation + SEPARATOR + location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetURL assetURL = (AssetURL) o;
        return domain.equals(assetURL.domain) &&
                location.equals(assetURL.location);
    }

    @Override
    public int hashCode() {
        return domain.hashCode() * 31 + location.hashCode();
    }

    @Override
    public String toString() {
        return domain + ":" + location;
    }
}
