package unknowndomain.engine.api.resource;

import unknowndomain.engine.api.util.DomainedPath;


public abstract class ResourceBase implements Resource {
    private DomainedPath location;

    public ResourceBase(DomainedPath location) {
        this.location = location;
    }

    public DomainedPath location() {
        return location;
    }
}
