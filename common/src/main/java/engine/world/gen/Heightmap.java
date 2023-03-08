package engine.world.gen;

import engine.block.state.BlockState;
import engine.registry.Registries;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkColumn;

import java.util.Objects;
import java.util.function.Predicate;

public class Heightmap {
    public static final Predicate<BlockState> NOT_AIR_PREDICATE = block -> !Objects.equals(Registries.getBlockRegistry().air(), block.getPrototype());
    private final int xSize;
    private final int zSize;

    private final int[] highestPos;

    private final Predicate<BlockState> validBlock;

    private ChunkColumn chunkColumn;

    protected Heightmap(int xSize, int zSize, Predicate<BlockState> validBlock) {
        this.xSize = xSize;
        this.zSize = zSize;
        this.validBlock = validBlock;
        this.highestPos = new int[xSize * zSize];
    }

    public static Heightmap create(ChunkColumn column, Predicate<BlockState> predicate) {
        var heightmap = new Heightmap(Chunk.CHUNK_X_SIZE, Chunk.CHUNK_Z_SIZE, predicate);
        heightmap.chunkColumn = column;
        return heightmap;
    }

    /**
     * @param x     position of changing block
     * @param y     position of changing block
     * @param z     position of changing block
     * @param state new block state
     * @return true if highest position has changed. otherwise false..
     */
    public boolean updatePos(int x, int y, int z, BlockState state) {
        var highestPosition = getHighestPosition(x, z);
        if (highestPosition > y) return false;
        if (validBlock.test(state)) {
            if (highestPosition < y) {
                setHighestPosition(x, z, y);
                return true;
            }
        } else if (highestPosition == y) {
            if (chunkColumn != null) {
                for (int i = y - 1; i >= 0; i--) {
                    var block = chunkColumn.getChunk(i / Chunk.CHUNK_Y_SIZE).getBlock(x, i, z);
                    if (validBlock.test(block)) {
                        setHighestPosition(x, z, i);
                        return true;
                    }
                }
            }
            setHighestPosition(x, z, 0);
            return true;
        }
        return false;
    }

    /**
     * @param x
     * @param z
     * @return ypos of the top block
     */
    public int getHighestPosition(int x, int z) {
        if (x < 0 || x >= xSize) throw new IndexOutOfBoundsException("xpos: " + x);
        if (z < 0 || z >= zSize) throw new IndexOutOfBoundsException("zpos: " + z);
        return highestPos[getIndex(x, z)];
    }

    public void setHighestPosition(int x, int z, int y) {
        if (x < 0 || x >= xSize) throw new IndexOutOfBoundsException("xpos: " + x);
        if (z < 0 || z >= zSize) throw new IndexOutOfBoundsException("zpos: " + z);
        highestPos[getIndex(x, z)] = y;
    }

    public int getIndex(int x, int z) {
        return x + z * xSize;
    }
}
