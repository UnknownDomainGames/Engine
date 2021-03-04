package engine.world.gen;

import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class ChunkGeneratorNodeInfo {
    private ChunkStatus status;

    private BiConsumer<Chunk, GeneratorContext> generateDelegate;

    public ChunkGeneratorNodeInfo(ChunkStatus status, BiConsumer<Chunk, GeneratorContext> generateDelegate) {
        this.status = status;
        this.generateDelegate = generateDelegate;
    }

    private Set<ChunkStatus> dependencies = new HashSet<>();

    public void dependsOn(ChunkStatus status) {
        dependencies.add(status);
    }

    public ChunkStatus getStatus() {
        return status;
    }

    public BiConsumer<Chunk, GeneratorContext> getGenerateDelegate() {
        return generateDelegate;
    }

    public Set<ChunkStatus> getDependencies() {
        return dependencies;
    }
}
