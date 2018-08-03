package unknowndomain.engine.client.resource.pipeline;

import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.client.model.MeshToGLNode;

public class MinecraftPipeline {
    public static ResourcePipeline create(ResourceManager manager) {
        ResourcePipeline pipeline = new ResourcePipeline(manager);
        pipeline.add("BakeModels", new ResolveModelsNode())
                .add("BakeModels", new ResolveTextureUVNode())
                .add("BakeModels", new BakeMeshNode());
        return pipeline;
    }
}
