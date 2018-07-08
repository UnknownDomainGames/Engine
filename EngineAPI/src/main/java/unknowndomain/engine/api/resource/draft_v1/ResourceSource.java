package unknowndomain.engine.api.resource.draft_v1;

import unknowndomain.engine.api.resource.ResourceLocation;

public interface ResourceSource {
    Resource load(ResourceLocation path);

    String[] domains();

    PackInfo info();

    String type();
}
