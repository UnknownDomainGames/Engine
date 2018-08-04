package unknowndomain.engine.api.resource;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceBase implements Resource {
    private ResourcePath location;
    private byte[] cache;

    public ResourceBase(ResourcePath location) {
        this.location = location;
    }

    public ResourcePath location() {
        return location;
    }

    @Override
    public byte[] cache() throws IOException {
        if (cache != null) return cache;
        InputStream open = this.open();
        if (open != null) {
            cache = IOUtils.toByteArray(open);
            open.close();
            return cache;
        }
        return null;
    }

    @Override
    public void invalidate() {
        this.cache = null;
    }
}
