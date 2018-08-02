package unknowndomain.engine.client.model;

import org.junit.jupiter.api.Test;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.client.texture.TextureManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftModelLoaderTest {

    @Test
    public void resolve() throws IOException {
        ResourceManager manager = new ResourceManager();
        TextureManager textureManager = new TextureManager(manager);
        manager.addResourceSource(new ResourceSourceBuiltin());
        MinecraftModelLoader loader = new MinecraftModelLoader(manager, textureManager);
        MinecraftModelLoader.Model resolve = loader.resolve(new DomainedPath("", "minecraft/models/block/sand.json"));
        assertNotNull(resolve, "Should resolve object");
    }
}