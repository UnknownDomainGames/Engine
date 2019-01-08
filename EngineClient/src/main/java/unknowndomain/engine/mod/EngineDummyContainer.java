package unknowndomain.engine.mod;

import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import unknowndomain.engine.Engine;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.input.controller.MotionType;
import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.block.ModelBlockRenderer;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.client.rendering.gui.GuiRenderer;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.event.EngineEvent;
import unknowndomain.engine.event.EngineEvent.ModInitializationEvent;
import unknowndomain.engine.event.registry.ClientRegistryEvent;
import unknowndomain.engine.game.Game;
import unknowndomain.game.Blocks;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;

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
        RegistryManager registryManager = e.getRegistryManager();
        registerBlocks(registryManager.getRegistry(Block.class));
        registerKeyBindings(registryManager.getRegistry(KeyBinding.class));

    }

    private void registerBlocks(Registry<Block> registry) {
        registry.register(Blocks.AIR);
        registry.register(Blocks.GRASS);
        registry.register(Blocks.DIRT);
    }

    private void registerKeyBindings(Registry<KeyBinding> registry) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        // ClientContext is fixed
        registry.register(
                KeyBinding.create("player.move.forward", Key.KEY_W, (c) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.FORWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.FORWARD, false)));
        registry.register(
                KeyBinding.create("player.move.backward", Key.KEY_S, (c) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.BACKWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.BACKWARD, false)));
        registry.register(KeyBinding.create("player.move.left", Key.KEY_A, (c) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.LEFT, true), ActionMode.PRESS)
                .endAction((c, i) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.LEFT, false)));
        registry.register(KeyBinding.create("player.move.right", Key.KEY_D, (c) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.RIGHT, true), ActionMode.PRESS)
                .endAction((c, i) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.RIGHT, false)));
        registry.register(KeyBinding.create("player.move.jump", Key.KEY_SPACE, (c) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.UP, true), ActionMode.PRESS)
                .endAction((c, i) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.UP, false)));
        registry.register(
                KeyBinding.create("player.move.sneak", Key.KEY_LEFT_SHIFT, (c) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.DOWN, true), ActionMode.PRESS)
                        .endAction((c, i) -> ((GameClientStandalone) c.getGame()).getEntityController().handleMotion(MotionType.DOWN, false)));
        registry.register(KeyBinding.create("player.mouse.left", Key.MOUSE_BUTTON_LEFT, (c) -> {
            BlockPrototype.Hit hit = c.getHit();
            if (hit != null) {
                c.getClientWorld().setBlock(hit.getPos(), Blocks.AIR);
            }
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("player.mouse.r", Key.MOUSE_BUTTON_RIGHT, (c) -> {
            BlockPrototype.Hit hit = c.getHit();
            if (hit != null) {
                c.getClientWorld().setBlock(hit.getFace().offset(hit.getPos()), Blocks.DIRT);
            }
        }, ActionMode.PRESS));
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

        TextureManager textureManager = e.getTextureManager();
        TextureUV side = textureManager.register(new ResourcePath("/assets/unknowndomain/textures/block/side.png"), BLOCK);
        TextureUV top = textureManager.register(new ResourcePath("/assets/unknowndomain/textures/block/top.png"), BLOCK);
        TextureUV bottom = textureManager.register(new ResourcePath("/assets/unknowndomain/textures/block/bottom.png"), BLOCK);

        BlockModel blockModel = new BlockModel();
        blockModel.addCube(0, 0, 0, 1, 1, 1, new TextureUV[] { side, side, side, side, top, bottom });
        ModelBlockRenderer.blockModelMap.put(Blocks.GRASS, blockModel);

        blockModel = new BlockModel();
        blockModel.addCube(0, 0, 0, 1, 1, 1, bottom);
        ModelBlockRenderer.blockModelMap.put(Blocks.DIRT, blockModel);
    }

    @Listener
    public void onEngineInitialized(EngineEvent.InitializationComplete e) {
        Game game = e.getEngine().startGame(null);
        game.run();
    }
}
