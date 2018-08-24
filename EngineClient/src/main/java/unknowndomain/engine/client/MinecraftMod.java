package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockBuilder;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.MinecraftModelFactory;
import unknowndomain.engine.client.rendering.RendererDebug;
import unknowndomain.engine.client.rendering.RendererContext;
import unknowndomain.engine.client.rendering.gui.RendererGui;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBuilder;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.World;

import java.util.ArrayList;
import java.util.List;

public class MinecraftMod {
    private GLTexture textureMap;
    private GLMesh[] meshRegistry;

    @Listener
    public void registerEvent(RegisterEvent event) {

    }

    void setupResource(GameContext context, ResourceManager manager, RendererContext renderer) throws Exception {
        Registry<Block> registry = context.getRegistry().getRegistry(Block.class);
        List<ResourcePath> pathList = new ArrayList<>();
        for (Block value : registry.getValues()) {
            if (value.getRegisteredName().equals("air"))
                continue;
            String path = "/minecraft/models/block/" + value.getRegisteredName() + ".json";
            pathList.add(new ResourcePath(path));
        }

        MinecraftModelFactory.Result feed = MinecraftModelFactory.process(manager, pathList);
        textureMap = feed.textureMap;
        meshRegistry = feed.meshes.stream().map(GLMesh::of).toArray(GLMesh[]::new);

        RendererDebug debug = new RendererDebug();
        debug.setTexture(textureMap);
        debug.setMeshRegistry(meshRegistry);
        renderer.add(debug);
        context.register(debug);
//        RendererSkybox skybox = new RendererSkybox();
//        renderer.add(skybox);
        RendererGui gui = new RendererGui();
        renderer.add(gui);
    }

    private Item createPlace(Block object) {
        class PlaceBlock implements ItemPrototype.UseBlockBehavior {
            private Block object;

            private PlaceBlock(Block object) {
                this.object = object;
            }

            @Override
            public void onUseBlockStart(World world, Entity entity, Item item, BlockPrototype.Hit hit) {
                BlockPos side = hit.face.side(hit.position);
                System.out.println("HIT: " + hit.position + " " + hit.face + " " + hit.hit + " SIDE: " + side);
                world.setBlock(side, object);
            }
        }
        return ItemBuilder.create(object.getRegisteredName() + "_placer").setUseBlockBehavior(new PlaceBlock(object))
                .build();
    }

    void postInit(GameContext context) {
        Registry<Item> itemRegistry = context.getItemRegistry();
        Registry<Block> blockRegistry = context.getBlockRegistry();
        Item stone = itemRegistry.getValue("stone_placer");
        UnknownDomain.getEngine().getController().getPlayer().getMountingEntity().getBehavior(Entity.TwoHands.class).setMainHand(stone);
        UnknownDomain.getGame().getWorld().setBlock(new BlockPos(1, 0, 0), blockRegistry.getValue(1));
    }

    void init(GameContext context) {
        Registry<Block> blockRegistry = context.getBlockRegistry();
        blockRegistry.register(BlockBuilder.create("air").setNoCollision().build());
        blockRegistry.register(BlockBuilder.create("stone").build());
        blockRegistry.register(BlockBuilder.create("grass_normal").build());

        Registry<Item> itemRegistry = context.getItemRegistry();
        itemRegistry.register(createPlace(blockRegistry.getValue("stone")));
        itemRegistry.register(createPlace(blockRegistry.getValue("grass_normal")));
    }
}
