package unknowndomain.engine.world;

import unknowndomain.engine.Prototype;
import unknowndomain.engine.RuntimeContext;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.Chunk;
import unknowndomain.engine.world.World;

public interface ChunkProvider {
    Chunk provideChunk(RuntimeContext runtimeContext, BlockPos pos);
}
