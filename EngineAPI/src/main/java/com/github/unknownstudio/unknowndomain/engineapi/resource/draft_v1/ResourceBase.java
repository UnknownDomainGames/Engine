package com.github.unknownstudio.unknowndomain.engineapi.resource.draft_v1;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

public abstract class ResourceBase implements Resource {
    private ResourceLocation location;

    public ResourceBase(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation location() {
        return location;
    }
}
