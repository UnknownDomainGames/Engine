package nullengine.world.chunk;

import nullengine.block.AirBlock;
import nullengine.block.Block;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;

import static nullengine.world.chunk.ChunkConstants.*;

public class AirChunk implements Chunk {

    private final World world;
    private final ChunkPos pos;

    private final Vector3ic min;
    private final Vector3ic max;
    private final Vector3ic center;

    public AirChunk(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.pos = ChunkPos.of(chunkX, chunkY, chunkZ);
        this.min = new Vector3i(chunkX << CHUNK_X_BITS, chunkY << CHUNK_Y_BITS, chunkZ << CHUNK_Z_BITS);
        this.max = min.add(CHUNK_X_SIZE, CHUNK_Y_SIZE, CHUNK_Z_SIZE, new Vector3i());
        this.center = min.add(CHUNK_X_SIZE >> 1, CHUNK_Y_SIZE >> 1, CHUNK_Z_SIZE >> 1, new Vector3i());
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public ChunkPos getPos() {
        return null;
    }

    @Override
    public int getX() {
        return pos.x();
    }

    @Override
    public int getY() {
        return pos.y();
    }

    @Override
    public int getZ() {
        return pos.z();
    }

    @Nonnull
    @Override
    public Vector3ic getMin() {
        return min;
    }

    @Nonnull
    @Override
    public Vector3ic getMax() {
        return max;
    }

    @Nonnull
    @Override
    public Vector3ic getCenter() {
        return center;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return AirBlock.AIR;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return AirBlock.AIR.getId();
    }

    @Override
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        return AirBlock.AIR;
    }

    @Override
    public boolean isAirChunk() {
        return true;
    }
}
