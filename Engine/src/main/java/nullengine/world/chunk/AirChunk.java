package nullengine.world.chunk;

import nullengine.block.AirBlock;
import nullengine.block.Block;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;

public class AirChunk implements Chunk {

    private final World world;
    private final ChunkPos pos;

    private final Vector3fc min;
    private final Vector3fc max;

    public AirChunk(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.pos = ChunkPos.of(chunkX, chunkY, chunkZ);
        this.min = new Vector3f(chunkX << ChunkConstants.CHUNK_X_BITS, chunkY << ChunkConstants.CHUNK_Y_BITS, chunkZ << ChunkConstants.CHUNK_Z_BITS);
        this.max = min.add(16, 16, 16, new Vector3f());
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
