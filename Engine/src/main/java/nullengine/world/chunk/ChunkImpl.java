package nullengine.world.chunk;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ChunkImpl implements Chunk {

    private final World world;
    private final int chunkX;
    private final int chunkY;
    private final int chunkZ;

    private final Vector3fc min;
    private final Vector3fc max;

    private final List<Entity> entities = new ArrayList<>();

    private BlockStorage blockStorage;
    private int nonAirBlockCount = 0;

    public ChunkImpl(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.min = new Vector3f(chunkX << ChunkConstants.BITS_X, chunkY << ChunkConstants.BITS_Y, chunkZ << ChunkConstants.BITS_Z);
        this.max = min.add(16, 16, 16, new Vector3f());
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkY() {
        return chunkY;
    }

    @Override
    public int getChunkZ() {
        return chunkZ;
    }

    @Nonnull
    @Override
    public Vector3fc getMin() {
        return min;
    }

    @Nonnull
    @Override
    public Vector3fc getMax() {
        return max;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        if (blockStorage == null) {
            return Registries.getBlockRegistry().air();
        }

        return blockStorage.getBlock(x, y, z);
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        if (blockStorage == null) {
            return Registries.getBlockRegistry().air().getId();
        }

        return blockStorage.getBlockId(x, y, z);
    }

    @Override
    public Block setBlock(BlockPos pos, Block block, BlockChangeCause cause) {
        return setBlock(pos.getX(), pos.getY(), pos.getZ(), block);
    }

    @NonNull
    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    protected Block setBlock(int x, int y, int z, Block block) {
        if (blockStorage == null) {
            blockStorage = new BlockStorage(this);
        }

        if (block != Registries.getBlockRegistry().air()) {
            nonAirBlockCount++;
        } else {
            nonAirBlockCount--;
        }

        return blockStorage.setBlock(x, y, z, block);
    }

    @Override
    public boolean isAirChunk() {
        return nonAirBlockCount == 0;
    }
}
