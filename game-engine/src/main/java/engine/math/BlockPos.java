package engine.math;

import com.google.common.base.MoreObjects;
import engine.util.Direction;
import engine.world.chunk.ChunkConstants;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

public abstract class BlockPos extends Vector3iBase {

    public static final BlockPos ZERO = of(0, 0, 0);

    public static BlockPos of(int x, int y, int z) {
        return new Immutable(x, y, z);
    }

    public static BlockPos of(Vector3ic vec) {
        return of(vec.x(), vec.y(), vec.z());
    }

    public static BlockPos of(Vector3fc vector3f) {
        return of((int) Math.floor(vector3f.x()), (int) Math.floor(vector3f.y()), (int) Math.floor(vector3f.z()));
    }

    public static BlockPos of(Vector3dc vector3d) {
        return of((int) Math.floor(vector3d.x()), (int) Math.floor(vector3d.y()), (int) Math.floor(vector3d.z()));
    }

    public static boolean inSameChunk(BlockPos a, BlockPos b) {
        return ((a.x() >> ChunkConstants.CHUNK_X_BITS) == (b.x() >> ChunkConstants.CHUNK_X_BITS)) && ((a.y() >> ChunkConstants.CHUNK_Y_BITS) == (b.y() >> ChunkConstants.CHUNK_Y_BITS)) && ((a.z() >> ChunkConstants.CHUNK_Z_BITS) == (b.z() >> ChunkConstants.CHUNK_Z_BITS));
    }

    public abstract BlockPos toImmutable();

    public abstract BlockPos add(int x, int y, int z);

    public abstract BlockPos sub(int x, int y, int z);

    public BlockPos offset(Direction direction) {
        return add(direction.offsetX, direction.offsetY, direction.offsetZ);
    }

    public BlockPos offset(Direction direction, int offset) {
        return add(direction.offsetX * offset, direction.offsetY * offset, direction.offsetZ * offset);
    }

    public BlockPos north() {
        return offset(Direction.NORTH);
    }

    public BlockPos north(int offset) {
        return offset(Direction.NORTH, offset);
    }

    public BlockPos south() {
        return offset(Direction.SOUTH);
    }

    public BlockPos south(int offset) {
        return offset(Direction.SOUTH, offset);
    }

    public BlockPos east() {
        return offset(Direction.EAST);
    }

    public BlockPos east(int offset) {
        return offset(Direction.EAST, offset);
    }

    public BlockPos up() {
        return offset(Direction.UP);
    }

    public BlockPos up(int offset) {
        return offset(Direction.UP, offset);
    }

    public BlockPos down() {
        return offset(Direction.DOWN);
    }

    public BlockPos down(int offset) {
        return offset(Direction.DOWN, offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPos))
            return false;

        BlockPos others = (BlockPos) obj;

        return this.x() == others.x() && y() == others.y() && z() == others.z();
    }

    @Override
    public int hashCode() {
        return (this.x() * 31 + y()) * 31 + z();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.x()).add("y", y()).add("z", z()).toString();
    }

    private static final class Immutable extends BlockPos {
        private final int x, y, z;

        private Immutable(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public int z() {
            return z;
        }

        @Override
        public BlockPos toImmutable() {
            return this;
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            return BlockPos.of(this.x + x, this.y + y, this.z + z);
        }

        @Override
        public BlockPos sub(int x, int y, int z) {
            return BlockPos.of(this.x - x, this.y - y, this.z - z);
        }
    }

    public static class Mutable extends BlockPos {

        public int x, y, z;
        private BlockPos immutable;

        public Mutable(BlockPos pos) {
            this(pos.x(), pos.y(), pos.z());
        }

        public Mutable(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public int z() {
            return z;
        }

        @Override
        public BlockPos toImmutable() {
            if (immutable == null) {
                immutable = BlockPos.of(x, y, z);
            }
            return immutable;
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            this.x += x;
            this.y += y;
            this.z += z;
            immutable = null;
            return this;
        }

        @Override
        public BlockPos sub(int x, int y, int z) {
            this.x -= x;
            this.y -= y;
            this.z -= z;
            immutable = null;
            return this;
        }

        public void set(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            immutable = null;
        }

        public void set(BlockPos blockPos) {
            set(blockPos.x(), blockPos.y(), blockPos.z());
        }
    }
}
