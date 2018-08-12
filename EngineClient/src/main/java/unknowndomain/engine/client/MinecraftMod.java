package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.model.pipeline.ModelToMeshNode;
import unknowndomain.engine.client.model.pipeline.ResolveModelsNode;
import unknowndomain.engine.client.model.pipeline.ResolveTextureUVNode;
import unknowndomain.engine.client.rendering.RenderBoundingBox;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.rendering.shader.CreateShaderNode;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBuilder;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.unclassified.BlockBuilder;
import unknowndomain.engine.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinecraftMod {
    private GLTexture textureMap;
    private GLMesh[] meshRegistry;

    void setupRender(GameContext context, ResourceManager manager, RendererGlobal renderer) {
        Shader v = new Shader(0, ShaderType.VERTEX_SHADER);
        v.loadShader("assets/unknowndomain/shader/common.vert");
        Shader f = new Shader(0, ShaderType.FRAGMENT_SHADER);
        f.loadShader("assets/unknowndomain/shader/common.frag");
//        RenderDebug debug = new RenderDebug(v, f);
//        debug.setTexture(textureMap);
//        debug.setMeshRegistry(meshRegistry);
//        renderer.add(debug);
//        context.register(debug);

        v = new Shader(0, ShaderType.VERTEX_SHADER);
        v.loadShader("assets/unknowndomain/shader/frame.vert");
        f = new Shader(0, ShaderType.FRAGMENT_SHADER);
        f.loadShader("assets/unknowndomain/shader/frame.frag");
        RenderBoundingBox frame = new RenderBoundingBox(v, f);
        renderer.add(frame);
    }

    void setupResource(GameContext context, ResourceManager manager) throws Exception {
        Pipeline pipeline = new Pipeline(manager);
        pipeline.add("BlockModels", new ResolveModelsNode(), new ResolveTextureUVNode(), new ModelToMeshNode(),
                new MeshToGLNode())
                .add("Shader", new CreateShaderNode());

        Registry<Block> registry = context.getManager().getRegistry(Block.class);
        List<ResourcePath> pathList = new ArrayList<>();
        for (Block value : registry.getValues()) {
            if (value.getRegistryName().equals("air")) continue;
            String path = "/minecraft/models/block/" + value.getRegistryName() + ".json";
            pathList.add(new ResourcePath(path));
        }

        Map<String, Object> result = pipeline.push("BlockModels", pathList);
        textureMap = (GLTexture) result.get("TextureMap");
        List<GLMesh> meshList = (List<GLMesh>) result.get("BlockModels");
        meshRegistry = new GLMesh[meshList.size()];
        for (int i = 0; i < meshList.size(); i++) {
            meshRegistry[i] = meshList.get(i);
        }
    }

    private Item createPlace(Block object) {
        class PlaceBlock implements ItemPrototype.UseBlockBehavior {
            private Block object;

            private PlaceBlock(Block object) {
                this.object = object;
            }

            @Override
            public void onUseBlockStart(World world, Player player, Item item, BlockPrototype.Hit hit) {
                BlockPos side = hit.face.side(hit.position);
                System.out.println("HIT: " + hit.position + " " + hit.face + " " + hit.hit + " SIDE: " + side);
                world.setBlock(side, object);
            }
        }
        return ItemBuilder.create(object.getRegistryName() + "_placer")
                .setUseBlockBehavior(new PlaceBlock(object))
                .build();
    }

    void init(GameContext context) {
        IdentifiedRegistry<Block> blockRegistry = context.getBlockRegistry();
        blockRegistry.register(BlockBuilder.create("air").setNoCollision().build());
        blockRegistry.register(BlockBuilder.create("stone").build());

        IdentifiedRegistry<Item> itemRegistry = context.getItemRegistry();
        itemRegistry.register(createPlace(blockRegistry.getValue("stone")));
        // Block stone = blockRegistry.getValue(1);
        // world.setBlock(new BlockPos(0, 0, 0), stone);
        // world.setBlock(new BlockPos(2, 0, 0), stone);
        // world.setBlock(new BlockPos(3, 0, 0), stone);
        // world.addEntity(player);
    }

    void postInit(GameContext context) {
        IdentifiedRegistry<Item> itemRegistry = context.getItemRegistry();
        IdentifiedRegistry<Block> blockRegistry = context.getBlockRegistry();
        Item stone = itemRegistry.getValue("stone_placer");
        UnknownDomain.getEngine().getPlayer().setMainHand(stone);
        UnknownDomain.getEngine().getWorld().setBlock(new BlockPos(1, 0, 0), blockRegistry.getValue(1));
    }
}
