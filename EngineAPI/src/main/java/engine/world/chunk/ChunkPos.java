package engine.world.chunk;

import com.google.common.base.MoreObjects;
import engine.math.Vector3iBase;
import engine.util.Direction;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static engine.world.chunk.ChunkConstants.*;

public abstract class ChunkPos extends Vector3iBase {

    public static ChunkPos of(int x, int y, int z) {
        return new ChunkPos.Immutable(x, y, z);
    }

    public static ChunkPos fromBlock(int x, int y, int z) {
        return of(x >> CHUNK_X_BITS, y >> CHUNK_Y_BITS, z >> CHUNK_Z_BITS);
    }

    public static ChunkPos fromWorldPos(Vector3f vector3f) {
        return fromBlock((int) Math.floor(vector3f.x), (int) Math.floor(vector3f.y), (int) Math.floor(vector3f.z));
    }

    public static ChunkPos fromWorldPos(Vector3d vector3d) {
        return fromBlock((int) Math.floor(vector3d.x), (int) Math.floor(vector3d.y), (int) Math.floor(vector3d.z));
    }

    public abstract ChunkPos toUnmodifiable();

    public abstract ChunkPos add(int x, int y, int z);

    public abstract ChunkPos sub(int x, int y, int z);

    public ChunkPos offset(Direction direction) {
        return add(direction.offsetX, direction.offsetY, direction.offsetZ);
    }

    public ChunkPos offset(Direction direction, int offset) {
        return add(direction.offsetX * offset, direction.offsetY * offset, direction.offsetZ * offset);
    }

    public ChunkPos north() {
        return offset(Direction.NORTH);
    }

    public ChunkPos north(int offset) {
        return offset(Direction.NORTH, offset);
    }

    public ChunkPos south() {
        return offset(Direction.SOUTH);
    }

    public ChunkPos south(int offset) {
        return offset(Direction.SOUTH, offset);
    }

    public ChunkPos east() {
        return offset(Direction.EAST);
    }

    public ChunkPos east(int offset) {
        return offset(Direction.EAST, offset);
    }

    public ChunkPos up() {
        return offset(Direction.UP);
    }

    public ChunkPos up(int offset) {
        return offset(Direction.UP, offset);
    }

    public ChunkPos down() {
        return offset(Direction.DOWN);
    }

    public ChunkPos down(int offset) {
        return offset(Direction.DOWN, offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkPos))
            return false;

        ChunkPos others = (ChunkPos) obj;

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

    private static final class Immutable extends ChunkPos {
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
        public ChunkPos toUnmodifiable() {
            return this;
        }

        @Override
        public ChunkPos add(int x, int y, int z) {
            return ChunkPos.of(this.x + x, this.y + y, this.z + z);
        }

        @Override
        public ChunkPos sub(int x, int y, int z) {
            return ChunkPos.of(this.x - x, this.y - y, this.z - z);
        }
    }

    public static class Mutable extends ChunkPos {

        public int x, y, z;
        private ChunkPos immutable;

        public Mutable(ChunkPos pos) {
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
        public ChunkPos toUnmodifiable() {
            if (immutable == null) {
                immutable = ChunkPos.of(x, y, z);
            }
            return immutable;
        }

        @Override
        public ChunkPos add(int x, int y, int z) {
            this.x += x;
            this.y += y;
            this.z += z;
            immutable = null;
            return this;
        }

        @Override
        public ChunkPos sub(int x, int y, int z) {
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

        public void set(ChunkPos blockPos) {
            set(blockPos.x(), blockPos.y(), blockPos.z());
        }
    }
}
