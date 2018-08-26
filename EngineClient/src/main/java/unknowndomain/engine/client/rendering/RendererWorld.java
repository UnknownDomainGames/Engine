package unknowndomain.engine.client.rendering;

import com.google.common.collect.Maps;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.Engine;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.BlockChangeEvent;
import unknowndomain.engine.world.ChunkLoadEvent;
import unknowndomain.engine.world.WorldCommon;

import java.util.Map;

public class RendererWorld extends RendererShaderProgram {
    protected final int A_POSITION = 0;
    protected final int A_TEXTCOORD = 1;
    protected final int A_NORMAL = 2;
    protected final int A_COLOR = 3;

    private int u_Projection;
    private int u_View;
    private int u_Model;
    private GLTexture texture;
    private Map<ChunkPos, RenderChunk> loadChunk = Maps.newHashMap();
    private GLMesh[] meshRegistry;
    private Mesh[] meshes;

    private GLMesh textureMap;

    {
        textureMap = GLMesh.of(new Mesh(new float[]{0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 2, 0,},
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

        this.texture = texMap;
        this.meshRegistry = meshRegistry;
    }

    @Override
    public void render(Context context) {
        Camera camera = context.getCamera();
        WorldCommon world = UnknownDomain.getGame().getWorld();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        useProgram();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Shader.setUniform(u_Projection, context.getProjection().projection());

        Shader.setUniform(u_View, camera.view());

        texture.bind();
        Shader.setUniform(u_Model, new Matrix4f().setTranslation(2, 2, 2));
        textureMap.render();

        BlockPrototype.Hit hit = world.raycast(camera.getPosition(),
                camera.getFrontVector(), 5);

        loadChunk.forEach((pos, chunk) -> { // TODO modify here to the baked chunk
            for (int i = 0; i < 16; i++) {
                if (chunk.valid[i]) {
                    int[] blocks = chunk.blocks[i];
                    for (int j = 0; j < 4096; j++) {
                        if (blocks[j] == 0)
                            continue;
                        int id = blocks[j];
                        int cy = i * 16;
                        // int cz = pos & 0xFFFF;

                        int x = ((j >> 8) & 0xF) + (pos.getChunkX() * 16);
                        int y = ((j >> 4) & 0xF) + cy * 16;
                        int z = (j & 0xF) + pos.getChunkZ() * 16;
                        // System.out.println("Render block at " + x + " " + y + " " + z);
                        boolean picked = hit != null && hit.position.getX() == x && hit.position.getY() == y
                                && hit.position.getZ() == z;

                        Shader.setUniform(u_Model, new Matrix4f().setTranslation(x, y, z));
                        // Shader.setUniform(u_Model, new Matrix4f().setTranslation(0, 0, 0));

                        if (picked)
                            this.setUniform("u_Picked", 1);
                        meshRegistry[id - 1].render();
                        if (picked)
                            this.setUniform("u_Picked", 0);
                    }
                }
            }
        });
    }

    @Listener
    public void handleChunkLoad(ChunkLoadEvent event) {
        Engine.getLogger().info("CHUNK LOAD " + event.pos);
        ChunkPos pos = event.pos;
        RenderChunk chunk = new RenderChunk(event.blocks);
        loadChunk.put(pos, chunk);
    }

    @Listener
    public void handleBlockChange(BlockChangeEvent event) {
        Engine.getLogger().info("BLOCK CHANGE");
        BlockPos pos = event.pos;
        ChunkPos cp = pos.toChunk();
        // RenderChunk chunk = loadChunk.get(cp.compact());
        RenderChunk chunk = loadChunk.get(cp);
        if (chunk == null) {
            Engine.getLogger().error("WTF, The chunk load not report?");
            return;
        }

        int yIndex = (pos.getY() & 255) >> 4;
        int xIndex = pos.pack();
        Engine.getLogger().info(pos + " -> " + xIndex);

        chunk.blocks[yIndex][xIndex] = event.blockId;
        if (event.blockId != 0 && !chunk.valid[yIndex]) {
            chunk.valid[yIndex] = true;
        }
    }

    @Override
    protected void useProgram() {
        super.useProgram();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public void dispose() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(programId);
        programId = -1;
    }

    class RenderChunk {
        int[][] blocks;
        boolean[] valid = new boolean[16];

        public RenderChunk(int[][] blocks) {
            this.blocks = blocks;
            for (int i = 0; i < blocks.length; i++) {
                int[] ck = blocks[i];
                boolean valid0 = false;
                for (int j = 0; j < ck.length; j++) {
                    if (ck[j] != 0) {
                        valid0 = true;
                        break;
                    }
                }
                valid[i] = valid0;
            }
        }
    }
}