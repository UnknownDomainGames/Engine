package unknowndomain.engine.world;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.math.BlockPos;

public interface ChunkProvider {
    Chunk provideChunk(GameContext gameContext, BlockPos pos);
}
