package unknowndomain.engine.client.resource.pipeline;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;

import static org.junit.jupiter.api.Assertions.*;

class MinecraftPipelineTest {

    @Test
    void create() throws Exception {
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());
        ResourcePipeline pipeline = MinecraftPipeline.create(resourceManager);
        pipeline.push("ModelPaths", Lists.newArrayList(new DomainedPath("", "/minecraft/models/block/stone.json")));
    }
}