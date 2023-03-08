package engine.world.chunk;

import engine.block.AirBlock;
import engine.block.state.BlockState;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;

public class AirChunk implements Chunk {

    private final World world;
    private final Vector3ic pos;

    private final Vector3ic min;
    private final Vector3ic max;
    private final Vector3ic center;

    public AirChunk(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.pos = new Vector3i(chunkX, chunkY, chunkZ);
        this.min = new Vector3i(chunkX << CHUNK_X_BITS, chunkY << CHUNK_Y_BITS, chunkZ << CHUNK_Z_BITS);
        this.max = min.add(CHUNK_X_SIZE, CHUNK_Y_SIZE, CHUNK_Z_SIZE, new Vector3i());
        this.center = min.add(CHUNK_X_SIZE >> 1, CHUNK_Y_SIZE >> 1, CHUNK_Z_SIZE >> 1, new Vector3i());
    }

    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.EMPTY;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Nonnull
    @Override
    public Vector3ic getPos() {
        return pos;
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
    public BlockState getBlock(int x, int y, int z) {
        return AirBlock.AIR.getDefaultState();
    }

    @Override
    public BlockState setBlock(@Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull BlockChangeCause cause) {
        return AirBlock.AIR.getDefaultState();
    }

    @Override
    public boolean isAirChunk() {
        return true;
    }
}
