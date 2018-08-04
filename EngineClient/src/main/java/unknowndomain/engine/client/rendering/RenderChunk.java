package unknowndomain.engine.client.rendering;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import unknowndomain.engine.client.game.LocatedData;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
//import unknowndomain.engine.client.resource.

public class RenderChunk extends RendererShaderProgramCommon implements Pipeline.Endpoint {
    private LongObjectMap<GLMesh> loadedChunk = new LongObjectHashMap<>();

    public RenderChunk(Shader vertexShader, Shader fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    @Override
    public void render(Context context) {
        super.render(context);
        for (GLMesh value : loadedChunk.values()) {
            value.render();
        }
    }

    @Override
    public void accept(String source, Object content) {
        if (source.equals("Chunk")) {
            LocatedData<GLMesh> data = (LocatedData<GLMesh>) content;

            ChunkPos blockPos = data.pos.toChunk();

            long pos = blockPos.getX() << 16L | blockPos.getY() << 8L | blockPos.getZ();

            GLMesh old = loadedChunk.put(pos, data.value);
            if (old != null) {
                old.dispose();
            }
        } else if (source.equals("Block")) {
            LocatedData<GLMesh> data = (LocatedData<GLMesh>) content;
            ChunkPos cpos = data.pos.toChunk();
            long pos = cpos.getX() << 16L | cpos.getY() << 8L | cpos.getZ();
            GLMesh mesh = loadedChunk.get(pos);
            if (mesh != null) {
                BlockPos inChunk = data.pos.inChunk();
            }
//            loadedChunk.put()
        }
    }
}
