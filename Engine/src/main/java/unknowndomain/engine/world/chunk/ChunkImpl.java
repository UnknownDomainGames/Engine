package unknowndomain.engine.world.chunk;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkImpl implements Chunk {

    private final World world;
    private final int chunkX, chunkY, chunkZ;
    private final List<Entity> entities = new ArrayList<>();

    private BlockStorage blockStorage;
    private int nonAirBlockCount = 0;

    public ChunkImpl(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
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

    @Override
    public Block getBlock(int x, int y, int z) {
        if (blockStorage == null) {
            return getWorld().getGame().getContext().getBlockAir();
        }

        return blockStorage.getBlock(x, y, z);
    }

    @Override
    public int getRawData(int x, int y, int z) {
        return blockStorage.getRawData(x, y, z);
    }

    @Override
    public Block setBlock(BlockPos pos, Block block) {
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

        if (block != getWorld().getGame().getContext().getBlockAir()) {
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
