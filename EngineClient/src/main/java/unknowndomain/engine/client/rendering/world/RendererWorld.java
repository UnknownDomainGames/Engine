package unknowndomain.engine.client.rendering.world;

import com.google.common.collect.Maps;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.rendering.block.ModelBlockRenderer;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.client.rendering.display.Camera;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.model.GLMesh;
import unknowndomain.engine.client.rendering.model.Mesh;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.rendering.texture.GLTexturePart;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.world.chunk.RenderChunkTask;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.world.chunk.ChunkLoadEvent;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.BlockChangeEvent;

import java.io.IOException;
import java.util.Map;

public class RendererWorld extends ShaderProgram {

    protected final int A_POSITION = 0;
    protected final int A_TEXTCOORD = 1;
    protected final int A_NORMAL = 2;
    protected final int A_COLOR = 3;

    private int u_Projection;
    private int u_View;
    private int u_Model;
    private int u_Texture;
    private GLTexture texture;
    private Map<ChunkPos, RenderChunkTask> loadChunk = Maps.newHashMap();
    private GLMesh[] meshRegistry;
    private Mesh[] meshes;

    private GLMesh textureMap;

    private ModelBlockRenderer modelBlockRenderer = new ModelBlockRenderer();
    private BlockModel blockModel;
    private GLTexture texture2;

    {
        textureMap = GLMesh.of(new Mesh(new float[]{0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0,},
                new float[]{0, 1, 1, 1, 1, 0, 0, 0,}, new float[]{

        }, new int[]{0, 2, 1, 0, 3, 2}, GL11.GL_TRIANGLES));
    }

    public RendererWorld(Shader vertex, Shader frag, GLTexture texMap, GLMesh[] meshRegistry, Mesh[] meshes) {
        this.meshes = meshes;
        createShader(vertex, frag);

        useProgram();
        u_Projection = getUniformLocation("u_ProjMatrix");
        u_View = getUniformLocation("u_ViewMatrix");
        u_Model = getUniformLocation("u_ModelMatrix");
        u_Texture = getUniformLocation("u_Texture");

        this.texture = texMap;
        this.meshRegistry = meshRegistry;

        try {
            texture = GLTexture.ofPNG(this.getClass().getResourceAsStream("/assets/unknowndomain/textures/block/stone.png"));
            texture2 = GLTexture.ofPNG(this.getClass().getResourceAsStream("/assets/unknowndomain/textures/block/grassblock.png"));
            blockModel = new BlockModel();
            blockModel.addCube(0, 0, 0, 1, 1, 1, new GLTexturePart(texture2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Context context) {
        Camera camera = context.getCamera();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        useProgram();

        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Shader.setUniform(u_Projection, context.getWindow().projection());

        Shader.setUniform(u_View, camera.view());

        Shader.setUniform(u_Model, new Matrix4f().setTranslation(camera.getLookAt()));

//        texture.bind();
//        Shader.setUniform(u_Model, new Matrix4f().setTranslation(2, 2, 2));
//        textureMap.render();

//        BlockPrototype.Hit hit = world.raycast(camera.getPosition(),
//                camera.getFrontVector(), 5);

//        loadChunk.forEach((pos, chunk) -> chunk.render(context));

//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        Shader.setUniform(u_Texture, 0);

        texture.bind();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, true, false, true);
        modelBlockRenderer.renderModel(blockModel, BlockPos.ZERO, tessellator.getBuffer());
        tessellator.draw();
//
//        buffer.begin(GL11.GL_TRIANGLES, true, false, true);
//        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
//        buffer.pos(1, 0, 0).tex(1, 1).endVertex();
//        buffer.pos(1, 1, 0).tex(0, 1).endVertex();
//        buffer.pos(0, 1, 0).tex(1, 1).endVertex();
//        tessellator.draw();

//        buffer.begin(GL11.GL_QUADS, true, false, true, false, false);
//        buffer.pos(0,0,0).tex(0,1).endVertex();
//        buffer.pos(1,0,0).tex(1,1).endVertex();
//        buffer.pos(1,1,0).tex(1,0).endVertex();
//        buffer.pos(0,1,0).tex(0,0).endVertex();
//        buffer.indices(0, 2, 1, 0, 3, 2);
//        tessellator.draw();

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Listener
    public void handleChunkLoad(ChunkLoadEvent event) {
        Engine.getLogger().info("CHUNK LOAD " + event.getPos());
        ChunkPos pos = event.getPos();
//        RenderChunkTask chunk = new RenderChunkTask(event.getWorld(), pos, event.getChunk());
//        chunk.markDirty();
//        loadChunk.put(pos, chunk);
    }

    @Listener
    public void handleBlockChange(BlockChangeEvent event) {
        Engine.getLogger().info("BLOCK CHANGE");
        BlockPos pos = event.getPos();
        ChunkPos cp = pos.toChunkPos();
        // RenderChunkTask chunk = loadChunk.get(cp.compact());
        RenderChunkTask chunk = loadChunk.get(cp);
        if (chunk == null) {
            Engine.getLogger().error("WTF, The chunk load not report?");
            return;
        }

//        chunk.markDirty();

//        int yIndex = (pos.getY() & 255) >> 4;
//        int xIndex = pos.pack();
//        Engine.getLogger().info(pos + " -> " + xIndex);
//
//        chunk.blocks[yIndex][xIndex] = event.blockId;
//        if (event.blockId != 0 && !chunk.valid[yIndex]) {
//            chunk.valid[yIndex] = true;
//        }
    }
}