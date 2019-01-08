package unknowndomain.game;

import org.slf4j.Logger;
import unknowndomain.engine.Engine;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.block.ClientBlockAir;
import unknowndomain.engine.client.block.ClientBlockDefault;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.input.controller.MotionType;
import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
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
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.IdAutoIncreaseRegistry;

import java.nio.ByteBuffer;

import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public final class DefaultGameMode {

    @Listener
    public void initializeMod(EngineEvent.ModInitializationEvent e) {
        Logger l = Engine.getLogger();
        l.info("Initializing EngineDummyContainer");
    }

    @Listener
    public void constructionRegistry(EngineEvent.RegistryConstructionEvent e) {
        // TODO: move to common.
        e.register(new IdAutoIncreaseRegistry<>(Block.class));

        e.register(new IdAutoIncreaseRegistry<>(KeyBinding.class));
        e.register(new IdAutoIncreaseRegistry<>(ClientBlock.class));
    }

    @Listener
    public void registerStage(EngineEvent.RegistrationStart e) {
        // register blocks
        RegistryManager registryManager = e.getRegistryManager();
        registerBlocks(registryManager.getRegistry(Block.class));
        registerKeyBindings(registryManager.getRegistry(KeyBinding.class));
        registerClientBlock(registryManager.getRegistry(ClientBlock.class));
    }

    private void registerBlocks(Registry<Block> registry) {
        registry.register(Blocks.AIR);
        registry.register(Blocks.GRASS);
        registry.register(Blocks.DIRT);
    }

    private void registerClientBlock(Registry<ClientBlock> registry) {
        registry.register(new ClientBlockAir(Blocks.AIR));
        registry.register(new ClientBlockDefault(Blocks.GRASS));
        registry.register(new ClientBlockDefault(Blocks.DIRT));
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
        ClientBlockDefault.blockModelMap.put(Blocks.GRASS, blockModel);

        blockModel = new BlockModel();
        blockModel.addCube(0, 0, 0, 1, 1, 1, bottom);
        ClientBlockDefault.blockModelMap.put(Blocks.DIRT, blockModel);
    }

    @Listener
    public void onEngineInitialized(EngineEvent.InitializationComplete e) {
        Game game = e.getEngine().startGame(null);
        game.run();
    }
}
