package unknowndomain.engine.client.model.pipeline;

import org.junit.jupiter.api.Test;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;

class MinecraftPipelineTest {

    @Test
    void create() throws Exception {
        ResourceManagerImpl resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());
//        Pipeline pipeline = MinecraftPipeline.create(resourceManager);
//        pipeline.push("ModelPaths", Lists.newArrayList(new ResourcePath("", "/minecraft/models/block/stone.json")));
    }
}