package unknowndomain.engine.mod;

import java.net.URL;

public class ModIdentity {
    private final String group, id, version;

    public ModIdentity(String group, String id, String version) {
        this.group = group;
        this.id = id;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String toString() {
        return group + ":" + id + ":" + version;
    }

    public URL toURL() {
        return null; // TODO implement this
    }
}