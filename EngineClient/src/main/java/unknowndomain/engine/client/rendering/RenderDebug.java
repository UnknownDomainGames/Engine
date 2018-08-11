package unknowndomain.engine.client.rendering;

import com.google.common.collect.Maps;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.LogicChunk;
import unknowndomain.engine.world.LogicWorld;

import java.util.Map;

public class RenderDebug extends RendererShaderProgramCommon implements Pipeline.Endpoint {
    private GLTexture texture;
    //    private IntObjectMap<RenderChunk> loadChunk = new IntObjectHashMap<>(16);
    private Map<ChunkPos, RenderChunk> loadChunk = Maps.newHashMap();
    private GLMesh[] meshRegistry;

    private GLMesh textureMap;

    {
        textureMap = new MeshToGLNode().convert(new Mesh(new float[]{
                0, 0, 0,
                2, 0, 0,
                2, 2, 0,
                0, 2, 0,
        }, new float[]{
                0, 1,
                1, 1,
                1, 0,
                0, 0,
        }, new float[]{

        }, new int[]{
                0, 2, 1, 0, 3, 2
        }, GL11.GL_TRIANGLES));
    }

    public RenderDebug(Shader vertexShader, Shader fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    @Override
    public void render(Context context) {
        super.render(context);

        if (texture != null)
            texture.bind();

        Shader.setUniform(u_Model, new Matrix4f().setTranslation(1, 1, 1));

//        for (GLMesh mesh : meshRegistry) {
//            if (mesh != null)
//                mesh.render();
//        }
        Shader.setUniform(u_Model, new Matrix4f().setTranslation(2, 2, 2));
        textureMap.render();
        BlockPrototype.Hit hit = UnknownDomain.getEngine().getWorld().rayHit(context.getCamera().getPosition(), context.getCamera().getFrontVector(), 5);


        loadChunk.forEach((pos, chunk) -> {
            for (int i = 0; i < 16; i++) {
                if (chunk.valid[i]) {
                    int[] blocks = chunk.blocks[i];
                    for (int j = 0; j < 4096; j++) {
                        if (blocks[j] == 0) continue;
                        int id = blocks[j];
                        int cy = i * 16;
//                        int cz = pos & 0xFFFF;

                        int x = ((j >> 8) & 0xF) + (pos.getChunkX() * 16);
                        int y = ((j >> 4) & 0xF) + cy * 16;
                        int z = (j & 0xF) + pos.getChunkZ() * 16;
//                        System.out.println("Render block at " + x + " " + y + " " + z);
                        boolean picked = hit != null && hit.position.getX() == x && hit.position.getY() == y && hit.position.getZ() == z;

                        Shader.setUniform(u_Model, new Matrix4f().setTranslation(x, y, z));
//                        Shader.setUniform(u_Model, new Matrix4f().setTranslation(0, 0, 0));

                        if (picked) this.setUniform("u_Picked", 1);
                        meshRegistry[id - 1].render();
                        if (picked) this.setUniform("u_Picked", 0);
                    }
                }
            }
        });
    }

    /**
     * @return the texture
     */
    public GLTexture getTexture() {
        return texture;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(GLTexture texture) {
        this.texture = texture;
    }

    /**
     * @param meshRegistry the meshRegistry to set
     */
    public void setMeshRegistry(GLMesh[] meshRegistry) {
        this.meshRegistry = meshRegistry;
    }

    @Listener
    public void handleChunkLoad(LogicWorld.ChunkLoad event) {
        System.out.println("CHUNK LOAD");
        ChunkPos pos = event.pos;
        RenderChunk chunk = new RenderChunk(event.blocks);
//        loadChunk.put(pos.compact(), chunk);
        loadChunk.put(pos, chunk);
    }

    @Listener
    public void handleBlockChange(LogicChunk.BlockChange event) {
        System.out.println("BLOCK CHANGE");
        BlockPos pos = event.pos;
        ChunkPos cp = pos.toChunk();
//        RenderChunk chunk = loadChunk.get(cp.compact());
        RenderChunk chunk = loadChunk.get(cp);
        if (chunk == null) {
            // Platform.getLogger().error("WTF, The chunk load not report?");
            return;
        }

        int yIndex = (pos.getY() & 255) >> 4;
        int xIndex = pos.pack();
        System.out.println(pos + " -> " + xIndex);

        chunk.blocks[yIndex][xIndex] = event.blockId;
        if (event.blockId != 0 && !chunk.valid[yIndex]) {
            chunk.valid[yIndex] = true;
        }
    }

    class RenderChunk {
        int[][] blocks;
        boolean[] valid = new boolean[16];

        public RenderChunk(int[][] blocks) {
            this.blocks = blocks;
            for (int i = 0; i < blocks.length; i++) {
                int[] ck = blocks[i];
                boolean none = true;
                for (int j = 0; j < ck.length; j++) {
                    if (ck[j] == 0) {
                        none = false;
                        break;
                    }
                }
                valid[i] = none;
            }
        }
    }

    @Override
    public void accept(String source, Object content) {
        switch (source) {
            case "TextureMap":
                this.texture = (GLTexture) content;
                break;
        }
    }
}