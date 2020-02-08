package engine.world.gen;

import engine.world.chunk.Chunk;

public interface ChunkGenerator {

    void generate(Chunk chunk);
}
