package com.github.unknownstudio.unknowndomain.engineapi.resource.draft_v1;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

public interface ResourceSource {
    Resource load(ResourceLocation path);

    String[] domains();

    PackInfo info();

    String type();
}
