package com.github.unknownstudio.unknowndomain.engineapi.resource.draft_v1;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface Resource {
    ResourceLocation location();

    InputStream open() throws IOException;

    // not impl yet
    // this api need more discussion...
//    Map<String, String> metadata();
}
