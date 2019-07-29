package nullengine.registry;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.Validate.notEmpty;

public class Name {

    private final String namespace;
    private final String name;
    private final String uniqueName;

    public static Name of(@Nonnull String namespace, @Nonnull String name) {
        return new Name(namespace, name);
    }

    public static Name fromString(@Nonnull String name) {
        String[] split = name.split(":");
        if (split.length == 2) {
            return new Name(split[0], split[1]);
        } else if (split.length == 1) {
            return new Name(Namespaces.getNamespace(), name);
        } else {
            throw new InvalidNameException(name);
        }
    }

    private Name(String namespace, String name) {
        this.namespace = notEmpty(namespace);
        this.name = notEmpty(name);
        this.uniqueName = namespace + ":" + name;
    }

    @Nonnull
    public String getNamespace() {
        return namespace;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name1 = (Name) o;
        return uniqueName.equals(name1.uniqueName);
    }

    @Override
    public int hashCode() {
        return uniqueName.hashCode();
    }

    @Override
    public String toString() {
        return uniqueName;
    }
}
