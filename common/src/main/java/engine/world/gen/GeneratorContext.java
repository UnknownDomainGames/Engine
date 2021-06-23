package engine.world.gen;

import engine.world.World;
import engine.world.WorldCreationSetting;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkColumn;
import engine.world.chunk.ChunkStatus;
import engine.world.chunk.DebugChunkManager;

import java.util.List;

public class GeneratorContext {
    private final World world;
    private final WorldCreationSetting setting;

    private Chunk targetChunk;
    private ChunkColumn targetChunkColumn;
    private List<ChunkStatus> statusOrder;

    public GeneratorContext(World world, WorldCreationSetting setting) {
        this.world = world;
        this.setting = setting;
    }

    public World getWorld() {
        return world;
    }

    public WorldCreationSetting getSetting() {
        return setting;
    }

    public Chunk getTargetChunk() {
        return targetChunk;
    }

    public ChunkColumn getTargetChunkColumn() {
        return targetChunkColumn;
    }

    public void setTargetChunk(Chunk targetChunk) {
        this.targetChunk = targetChunk;
        //TODO: generalize
        if (targetChunk.getWorld().getChunkManager() instanceof DebugChunkManager) {
            this.targetChunkColumn = ((DebugChunkManager) targetChunk.getWorld().getChunkManager()).getChunkColumn(targetChunk.getX(), targetChunk.getZ());
        }
    }

    public void setStatusOrder(List<ChunkStatus> statusOrder) {
        if (this.statusOrder == null) {
            this.statusOrder = statusOrder;
        }
    }

    public List<ChunkStatus> getStatusOrder() {
        return statusOrder;
    }
}
