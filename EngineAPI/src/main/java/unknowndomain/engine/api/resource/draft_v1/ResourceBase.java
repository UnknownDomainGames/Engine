package unknowndomain.engine.api.resource.draft_v1;

import unknowndomain.engine.api.resource.ResourceLocation;

public abstract class ResourceBase implements Resource {
    private ResourceLocation location;

    public ResourceBase(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation location() {
        return location;
    }
}
