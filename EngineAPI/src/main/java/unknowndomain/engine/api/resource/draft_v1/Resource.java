package unknowndomain.engine.api.resource.draft_v1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import unknowndomain.engine.api.resource.ResourceLocation;

public interface Resource {
    ResourceLocation location();

    InputStream open() throws IOException;

    // not impl yet
    // this api need more discussion...
//    Map<String, String> metadata();
}
