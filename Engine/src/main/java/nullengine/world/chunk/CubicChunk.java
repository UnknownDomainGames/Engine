package nullengine.world.chunk;

import nullengine.block.Block;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.world.World;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class CubicChunk implements Chunk {

    private final WeakReference<World> world;
    private final ChunkPos pos;

    private final Vector3fc min;
    private final Vector3fc max;

    private BlockStorage blockStorage;
    private int nonAirBlockCount = 0;

    public CubicChunk(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = new WeakReference<>(world);
        this.pos = ChunkPos.of(chunkX, chunkY, chunkZ);
        this.min = new Vector3f(chunkX << ChunkConstants.BITS_X, chunkY << ChunkConstants.BITS_Y, chunkZ << ChunkConstants.BITS_Z);
        this.max = min.add(16, 16, 16, new Vector3f());
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world.get();
    }

    @Override
    public ChunkPos getPos() {
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
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        return setBlock(pos.x(), pos.y(), pos.z(), block);
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

    public void write(DataOutput output) throws IOException {
        output.writeShort(nonAirBlockCount);

        if (nonAirBlockCount != 0) {
            long[] data = blockStorage.getData().getBackingArray();
            for (int i = 0; i < data.length; i++) {
                output.writeLong(data[i]);
            }
        }
    }

    public void read(DataInput input) throws IOException {
        nonAirBlockCount = input.readShort();

        if (nonAirBlockCount != 0) {
            blockStorage = new BlockStorage(this);
            long[] data = blockStorage.getData().getBackingArray();
            for (int i = 0; i < data.length; i++) {
                data[i] = input.readLong();
            }
        }
    }
}
