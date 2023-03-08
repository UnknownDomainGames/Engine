package engine.world.util;

import engine.math.BlockPos;
import engine.world.chunk.Chunk;
import org.joml.Vector3ic;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BlockPosIterator implements Iterator<BlockPos> {

    public static BlockPosIterator create(BlockPos from, BlockPos to) {
        return new BlockPosIterator(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
    }

    public static BlockPosIterator createFromChunk(Chunk chunk) {
        Vector3ic min = chunk.getMin();
        return new BlockPosIterator(min.x(), min.y(), min.z(),
                min.x() + Chunk.CHUNK_MAX_X, min.y() + Chunk.CHUNK_MAX_Y, min.z() + Chunk.CHUNK_MAX_Z);
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

    @Override
    public boolean hasNext() {
        return pos.y() != toY || pos.z() != toZ || pos.x() != toX;
    }

    /**
     * @throws NoSuchElementException
     */
    @Override
    public BlockPos next() {
        pos.add(1, 0, 0);
        if (pos.x() > toX) {
            pos.set(fromX, pos.y(), pos.z() + 1);
        }
        if (pos.z() > toZ) {
            pos.set(fromX, pos.y() + 1, fromZ);
        }
        if (pos.y() > toY) {
            throw new NoSuchElementException();
        }
        return pos;
    }

    public void reset() {
        pos.set(fromX - 1, fromY, fromZ);
    }
}
