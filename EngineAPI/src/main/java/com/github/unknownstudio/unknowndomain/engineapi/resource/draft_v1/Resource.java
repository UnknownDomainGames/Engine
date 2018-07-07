package com.github.unknownstudio.unknowndomain.engineapi.resource.draft_v1;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

import java.io.InputStream;
import java.util.Map;

public interface Resource {
    ResourceLocation location();

    InputStream open();

    Map<String, String> metadata();
}
