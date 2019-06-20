package unknowndomain.engine.mod;

import unknowndomain.engine.util.versioning.Version;

import java.util.Objects;

public class ModIdentifier {
    private final String id;
    private final Version version;

    protected ModIdentifier(String id, Version version) {
        this.id = id;
        this.version = version;
    }

    /**
     * TODO check the modid style
     */
    public static ModIdentifier of(String modid, Version version) {
        Objects.requireNonNull(modid);
        Objects.requireNonNull(version);
        return new ModIdentifier(modid, version);
    }

    public static ModIdentifier from(String s) {
        String[] split = s.split(":");
        if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty())
            throw new IllegalArgumentException("Invalid mod identifier syntax: " + s);
        return new ModIdentifier(split[0], new Version(split[1]));
    }

    public String getId() {
        return id;
    }

    public Version getVersion() {
        return version;
    }

    public String toString() {
        return id + ":" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModIdentifier)) return false;
        ModIdentifier that = (ModIdentifier) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return id.hashCode() * 31 + version.hashCode();
    }
}
