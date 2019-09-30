package nullengine.world.util;

import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import org.joml.Vector3ic;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static nullengine.world.chunk.ChunkConstants.*;

public class BlockPosIterator implements Iterator<BlockPos> {

    public static BlockPosIterator create(BlockPos from, BlockPos to) {
        return new BlockPosIterator(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
    }

    public static BlockPosIterator createFromChunk(Chunk chunk) {
        Vector3ic min = chunk.getMin();
        return new BlockPosIterator(min.x(), min.y(), min.z(),
                min.x() + CHUNK_MAX_X, min.y() + CHUNK_MAX_Y, min.z() + CHUNK_MAX_Z);
    }

    private final int fromX, fromY, fromZ, toX, toY, toZ;
    private final BlockPos.Mutable pos = new BlockPos.Mutable(0, 0, 0);

    public BlockPosIterator(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        this.fromX = min(fromX, toX);
        this.fromY = min(fromY, toY);
        this.fromZ = min(fromZ, toZ);
        this.toX = max(fromX, toX);
        this.toY = max(fromY, toY);
        this.toZ = max(fromZ, toZ);
        reset();
    }

    public boolean hasNext() {
        return pos.x() != toX || pos.y() != toY || pos.z() != toZ;
    }

    /**
     * @throws NoSuchElementException
     */
    public BlockPos next() {
        pos.add(1, 0, 0);
        if (pos.x() > toX) {
            pos.set(fromX, pos.y() + 1, pos.z());
        }
        if (pos.y() > toY) {
            pos.set(pos.x(), fromY, pos.z() + 1);
        }
        if (pos.z() > toZ) {
            throw new NoSuchElementException();
        }
        return pos;
    }

    public void reset() {
        pos.set(fromX - 1, fromY, fromZ);
    }

}
