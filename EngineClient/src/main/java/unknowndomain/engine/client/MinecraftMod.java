package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockBuilder;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.model.pipeline.ModelToMeshNode;
import unknowndomain.engine.client.model.pipeline.ResolveModelsNode;
import unknowndomain.engine.client.model.pipeline.ResolveTextureUVNode;
import unknowndomain.engine.client.rendering.RendererDebug;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.rendering.gui.RendererGui;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBuilder;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinecraftMod {
    private GLTexture textureMap;
    private GLMesh[] meshRegistry;

    void setupResource(GameContext context, ResourceManager manager, RendererGlobal renderer) throws Exception {
        Pipeline pipeline = new Pipeline(manager);
        pipeline.add("BlockModels", new ResolveModelsNode(), new ResolveTextureUVNode(), new ModelToMeshNode(),
                new MeshToGLNode());

        Registry<Block> registry = context.getManager().getRegistry(Block.class);
        List<ResourcePath> pathList = new ArrayList<>();
        for (Block value : registry.getValues()) {
            if (value.getRegisteredName().equals("air"))
                continue;
            String path = "/minecraft/models/block/" + value.getRegisteredName() + ".json";
            pathList.add(new ResourcePath(path));
        }

        Map<String, Object> result = pipeline.push("BlockModels", pathList);
        textureMap = (GLTexture) result.get("TextureMap");
        List<GLMesh> meshList = (List<GLMesh>) result.get("BlockModels");
        meshRegistry = new GLMesh[meshList.size()];
        for (int i = 0; i < meshList.size(); i++) {
            meshRegistry[i] = meshList.get(i);
        }

        RendererDebug debug = new RendererDebug();
        debug.setTexture(textureMap);
        debug.setMeshRegistry(meshRegistry);
        renderer.add(debug);
        context.register(debug);
//        RendererSkybox skybox = new RendererSkybox();
//        renderer.add(skybox);

        // v = new Shader(0, ShaderType.VERTEX_SHADER);
        // v.loadShader("assets/unknowndomain/shader/frame.vert");
        // f = new Shader(0, ShaderType.FRAGMENT_SHADER);
        // f.loadShader("assets/unknowndomain/shader/frame.frag");
        // RenderBoundingBox frame = new RenderBoundingBox(v, f);
        // renderer.add(frame);

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
        UnknownDomain.getEngine().getWorld().setBlock(new BlockPos(1, 0, 0), blockRegistry.getValue(1));
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

    public static class MeshToGLNode implements Pipeline.Node {
        @Override
        public Object process(Pipeline.Context context, Object in) {
            if (in instanceof Mesh) {
                return GLMesh.of((Mesh) in);
            } else if (in instanceof List) {
                List<Mesh> meshes = (List<Mesh>) in;
                List<GLMesh> glMeshes = new ArrayList<>();
                for (Mesh mesh : meshes) {
                    glMeshes.add(GLMesh.of(mesh));
                }
                return glMeshes;
            } else {
                return new ArrayList<Mesh>();
            }
        }
    }

}
