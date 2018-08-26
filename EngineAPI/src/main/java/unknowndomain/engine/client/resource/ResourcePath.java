package unknowndomain.engine.client.resource;

import com.google.common.base.Strings;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

/**
 * The resource path. I'm considering remove this thing....
 */
public class ResourcePath {
    private String type;
    private String path;

    public ResourcePath(String type, @Nonnull String path) {
        this.type = Strings.nullToEmpty(type);
        this.path = Validate.notEmpty(path);
    }

    public ResourcePath(@Nonnull String resource) {
        Validate.notEmpty(resource);

        String args[] = resource.split(":", 2);
        if (args.length < 2) {
            this.type = "";
            this.path = args[0];
        } else {
            this.type = args[0];
            this.path = args[1];
        }
    }

    public static ResourcePath of(String type, @Nonnull String path) {
        return new ResourcePath(type, path);
    }

    public static ResourcePath of(@Nonnull String path) {
        return new ResourcePath(path);
    }

    @Nonnull
    public String getType() {
        return type;
    }

    @Nonnull
    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ResourcePath))
            return false;

        ResourcePath other = (ResourcePath) obj;

        if (!type.equals(other.type))
            return false;
        if (!path.equals(other.path))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return Strings.isNullOrEmpty(getType()) ? getPath() : getType() + ":" + getPath();
    }

}
