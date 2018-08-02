package unknowndomain.engine.client.resource.pipeline;

import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.client.model.MeshToGLNode;

public class MinecraftPipeline {
    public static ResourcePipeline create(ResourceManager manager) {
        ResourcePipeline pipeline = new ResourcePipeline(manager);
        pipeline.add("ModelPaths", new ResolveModelsNode())
                .add("ResolvedModels", new ResolveTextureUVNode())
                .add("MappedResolvedModels", new BakeMeshNode())
                .add("BakedMeshes", new MeshToGLNode());
        return pipeline;
    }
}
