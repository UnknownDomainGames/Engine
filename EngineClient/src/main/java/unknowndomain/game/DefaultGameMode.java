package unknowndomain.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.rendering.block.ModelBlockRenderer;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.registry.ClientRegistryEvent;
import unknowndomain.engine.event.registry.GamePreInitializationEvent;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.event.registry.ResourceSetupEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBuilder;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public final class DefaultGameMode {

    @Listener
    @Deprecated
    public void registerEvent(RegisterEvent event) {
        // Moved to EngineDummyContainer
        event.getRegistry().register(Blocks.AIR);
        event.getRegistry().register(Blocks.GRASS);
        event.getRegistry().register(Blocks.DIRT);
    }

    @Listener
    @Deprecated
    public void preInit(GamePreInitializationEvent event) {
        // For now, this is set directly until we figure out how to initialize the game
        event.setBlockAir(Blocks.AIR);
    }

    @Listener
    @Deprecated
    public void clientRegisterEvent(ClientRegistryEvent event) {
        // Moved to EngineDummyContainer
//        event.registerRenderer((context, manager) ->
//                {
//                    ChunkRenderer chunkRenderer = new ChunkRenderer(
//                            Shader.create(manager.load(new ResourcePath("unknowndomain/shader/chunk_solid.vert")).cache(), ShaderType.VERTEX_SHADER),
//                            Shader.create(manager.load(new ResourcePath("unknowndomain/shader/chunk_solid.frag")).cache(), ShaderType.FRAGMENT_SHADER)
//                    );
//                    context.register(chunkRenderer);
//
//                    WorldRenderer worldRenderer = new WorldRenderer(
//                            Shader.create(manager.load(new ResourcePath("unknowndomain/shader/world.vert")).cache(), ShaderType.VERTEX_SHADER),
//                            Shader.create(manager.load(new ResourcePath("unknowndomain/shader/world.frag")).cache(), ShaderType.FRAGMENT_SHADER),
//                            chunkRenderer);
//                    return worldRenderer;
//                }
//        );
    }

    @Listener
    @Deprecated
    public void setupResource(ResourceSetupEvent event) {
        // Moved to Engine
        TextureManager textureManager = event.getTextureManager();
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

//    @Listener
//    public void onGameReady(GameReadyEvent event) {
//        GameContext context = event.getContext();
//        Registry<Item> itemRegistry = context.getItemRegistry();
//        Registry<Block> blockRegistry = context.getBlockRegistry();
//        Item stone = itemRegistry.getValue("minecraft.item.stone_placer");
//        UnknownDomain.getGame().getPlayer().getControlledEntity().getBehavior(TwoHands.class).setMainHand(stone);
////        UnknownDomain.getGame().getPlayer().getControlledEntity().getPosition().set(0, 2, 0);
//        UnknownDomain.getGame().getWorld().setBlock(BlockPos.of(1, 0, 0), blockRegistry.getValue(1));
//    }

    private Item createPlace(Block object, String name) {
        class PlaceBlock implements ItemPrototype.UseBlockBehavior {
            private Block object;

            private PlaceBlock(Block object) {
                this.object = object;
            }

            @Override
            public void onUseBlockStart(World world, Entity entity, Item item, BlockPrototype.Hit hit) {
                BlockPos side = hit.getFace().offset(hit.getPos());
                System.out.println("HIT: " + hit.getPos() + " " + hit.getFace() + " " + hit.getHit() + " SIDE: " + side);
                world.setBlock(side, object);
            }
        }
        return ItemBuilder.create(name + "_placer").setUseBlockBehavior(new PlaceBlock(object)).build();
    }
}
