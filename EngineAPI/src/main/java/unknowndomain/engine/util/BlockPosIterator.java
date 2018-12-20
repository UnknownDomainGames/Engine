package unknowndomain.engine.util;

import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;

import java.util.NoSuchElementException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BlockPosIterator {

    public static BlockPosIterator create(BlockPos from, BlockPos to) {
        return new BlockPosIterator(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    public static BlockPosIterator createFromChunkPos(ChunkPos chunkPos) {
        return new BlockPosIterator(chunkPos.getX() << 4, chunkPos.getY() << 4, chunkPos.getZ() << 4,
                (chunkPos.getX() << 4) + 15, (chunkPos.getY() << 4) + 15, (chunkPos.getZ() << 4) + 15);
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
        return pos.getX() != toX || pos.getY() != toY || pos.getZ() != toZ;
    }

    /**
     * @throws NoSuchElementException
     */
    public BlockPos next() {
        pos.add(1, 0, 0);
        if (pos.getX() > toX) {
            pos.set(fromX, pos.getY() + 1, pos.getZ());
        }
        if (pos.getY() > toY) {
            pos.set(pos.getX(), fromY, pos.getZ() + 1);
        }
        if (pos.getZ() > toZ) {
            throw new NoSuchElementException();
        }
        return pos;
    }

    public void reset() {
        pos.set(fromX - 1, fromY, fromZ);
    }

}
