package unknowndomain.engine.mod;

import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import unknowndomain.engine.Engine;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.GuiRenderer;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.event.EngineEvent;
import unknowndomain.engine.event.EngineEvent.ModInitializationEvent;
import unknowndomain.engine.event.registry.ClientRegistryEvent;
import unknowndomain.game.Blocks;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.registry.Registry;

public class EngineDummyContainer implements ModContainer {

    private final ModMetadata metadata = ModMetadata.Builder.create().setId("engine").setVersion("0.0.1").setGroup("none").build();

    @Override
    public String getModId() {
        return metadata.getId();
    }

    @Override
    public Object getInstance() {
        return this;
    }

    @Override
    public Logger getLogger() {
        return Engine.LOGGER;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    @Listener
    public void initializeMod(ModInitializationEvent e) {
        Logger l = getLogger();
        l.info("Initializing EngineDummyContainer");
    }

    @Listener
    public void registerStage(EngineEvent.RegistrationStart e) {
        // register blocks
        Registry<Block> registry = e.getRegistryManager().getRegistry(Block.class);
        registry.register(Blocks.AIR);
        registry.register(Blocks.GRASS);
        registry.register(Blocks.DIRT);
    }

    @Listener
    public void constructResource(EngineEvent.ResourceConstructionStart e) {

        e.registerRenderer((context, manager) -> {
            ChunkRenderer chunkRenderer = new ChunkRenderer(Shader.create(manager.load(new ResourcePath("unknowndomain/shader/chunk_solid.vert")).cache(), ShaderType.VERTEX_SHADER),
                    Shader.create(manager.load(new ResourcePath("unknowndomain/shader/chunk_solid.frag")).cache(), ShaderType.FRAGMENT_SHADER));
            context.register(chunkRenderer);

            WorldRenderer worldRenderer = new WorldRenderer(Shader.create(manager.load(new ResourcePath("unknowndomain/shader/world.vert")).cache(), ShaderType.VERTEX_SHADER),
                    Shader.create(manager.load(new ResourcePath("unknowndomain/shader/world.frag")).cache(), ShaderType.FRAGMENT_SHADER), chunkRenderer);
            return worldRenderer;
        });

        e.registerRenderer((context, manager) -> {
            Resource resource = manager.load(new ResourcePath("", "unknowndomain/fonts/arial.ttf"));
            byte[] cache = resource.cache();
            return new GuiRenderer(ByteBuffer.allocateDirect(cache.length).put(cache).flip(),
                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.vert")).cache(), ShaderType.VERTEX_SHADER),
                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.frag")).cache(), ShaderType.FRAGMENT_SHADER));
        });
    }

    @Listener
    public void onEngineInitialized(EngineEvent.InitializationComplete e) {
        e.getEngine().startGame(null);
    }
}
