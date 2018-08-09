package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.unclassified.BlockObjectBuilder;

public class MinecraftMod {
    void debug() {

    }

    void init(GameContext context) {
        IdentifiedRegistry<BlockObject> blockRegistry = context.getBlockRegistry();
        blockRegistry.register(BlockObjectBuilder.create("air").build());
        blockRegistry.register(BlockObjectBuilder.create("stone").build());
        
        // context.register(debug);
        // resourceManager.subscribe("TextureMap", debug);

        // List<GLMesh> meshList = resourceManager.push("BlockModels",
        //         Lists.newArrayList(new ResourcePath("", "/minecraft/models/block/stone.json")
        //         // new ResourcePath("", "/minecraft/models/block/sand.json"),
        //         // new ResourcePath("", "/minecraft/models/block/brick.json"),
        //         // new ResourcePath("", "/minecraft/models/block/clay.json"),
        //         // new ResourcePath("", "/minecraft/models/block/furnace.json")
        //         // new ResourcePath("", "/minecraft/models/block/birch_stairs.json"),
        //         // new ResourcePath("", "/minecraft/models/block/lever.json"),
        //         ));
        // GLMesh[] reg = new GLMesh[] { null, meshList.get(0) };
        // debug.setMesheRegistry(reg);

        // BlockObject stone = blockRegistry.getValue(1);
        // world.setBlock(new BlockPos(0, 0, 0), stone);
        // world.setBlock(new BlockPos(1, 0, 0), stone);
        // world.setBlock(new BlockPos(2, 0, 0), stone);
        // world.setBlock(new BlockPos(3, 0, 0), stone);
        // world.addEntity(player);

        for (BlockObject object : context.getBlockRegistry().getValues()) {
            String path = object.getRegistryName();
        }
    }
}
