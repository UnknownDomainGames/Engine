package engine.world.gen;

import engine.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

public interface ChunkGenerator {

    void generate(Chunk chunk);

    CompletableFuture<Chunk> generateAsync(Chunk chunk);
}
