package engine.world.gen;

import engine.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

public class ChunkGeneratorNode {

    private final ChunkGeneratorNodeInfo info;

    public ChunkGeneratorNode(ChunkGeneratorNodeInfo info) {
        this.info = info;
    }

    public final CompletableFuture<Chunk> processAsync(Chunk chunk, GeneratorContext context) {
        process(chunk, context);
        return CompletableFuture.completedFuture(chunk);
    }

    public void process(Chunk chunk, GeneratorContext context) {
        if (context.getStatusOrder().indexOf(chunk.getStatus()) < context.getStatusOrder().indexOf(info.getStatus())) {
            info.getGenerateDelegate().accept(chunk, context);
        }
    }

    public ChunkGeneratorNodeInfo getInfo() {
        return info;
    }

}
