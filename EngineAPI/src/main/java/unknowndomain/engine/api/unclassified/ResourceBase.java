package unknowndomain.engine.api.unclassified;

import java.net.URL;

public abstract class ResourceBase implements Resource {
    private URL location;

    public ResourceBase(URL location) {
        this.location = location;
    }

    public URL location() {
        return location;
    }
}
