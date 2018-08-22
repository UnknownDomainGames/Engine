package unknowndomain.engine.mod;

import java.util.Objects;

public class ModIdentifier {
    private String modid;
    private String version;

    private ModIdentifier(String modid, String version) {
        this.modid = modid;
        this.version = version;
    }

    /**
     * TODO check the modid style
     *
     * @param modid
     * @param version
     * @return
     */
    public static ModIdentifier of(String modid, String version) {
        Objects.requireNonNull(modid);
        Objects.requireNonNull(version);
        return new ModIdentifier(modid, version);
    }

    public static ModIdentifier from(String s) {
        String[] split = s.split(":");
        if (split.length != 2 || split[0].equals("") || split[1].equals(""))
            throw new IllegalArgumentException("Invalid mod identifier syntax: " + s);
        return new ModIdentifier(split[0], split[1]);
    }

    public String getModid() {
        return modid;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return modid + ":" + version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModIdentifier that = (ModIdentifier) o;
        return Objects.equals(modid, that.modid) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, version);
    }
}
