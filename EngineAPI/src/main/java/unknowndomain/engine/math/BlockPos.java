package unknowndomain.engine.math;

import com.google.common.base.MoreObjects;
import org.joml.Vector3d;
import org.joml.Vector3f;

public abstract class BlockPos {

    public static final BlockPos ZERO = of(0, 0, 0);

    public static BlockPos of(int x, int y, int z) {
        return new Immutable(x, y, z);
    }

    public static BlockPos of(Vector3f vector3f) {
        return of((int) Math.floor(vector3f.x), (int) Math.floor(vector3f.y), (int) Math.floor(vector3f.z));
    }

    public static BlockPos of(Vector3d vector3d) {
        return of((int) Math.floor(vector3d.x), (int) Math.floor(vector3d.y), (int) Math.floor(vector3d.z));
    }

    public static boolean inSameChunk(BlockPos a, BlockPos b) {
        return ((a.getX() >> 4) == (b.getX() >> 4)) && ((a.getY() >> 4) == (b.getY() >> 4)) && ((a.getZ() >> 4) == (b.getZ() >> 4));
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

    public abstract BlockPos toImmutable();

    public abstract BlockPos add(int x, int y, int z);

    public abstract BlockPos minus(int x, int y, int z);

    public ChunkPos toChunkPos() {
        return ChunkPos.fromBlockPos(this);
    }

    public int squareDistanceTo(BlockPos another) {
        int x = this.getX() - another.getX();
        int y = this.getY() - another.getY();
        int z = this.getZ() - another.getZ();
        return x * x + y * y + z * z;
    }

//    public BlockPos pack() {
//        return new BlockPos(x & 16, y & 16, z & 16);
//    }

    public int pack() {
        return ((getX() & 0xF) << 8) | ((getY() & 0xF) << 4) | (getZ() & 0xF);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPos))
            return false;

        BlockPos others = (BlockPos) obj;

        return getX() == others.getX() && getY() == others.getY() && getZ() == others.getZ();
    }

    @Override
    public int hashCode() {
        return (getX() * 31 + getY()) * 31 + getZ();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", getX()).add("y", getY()).add("z", getZ()).toString();
    }

    private static final class Immutable extends BlockPos {
        private final int x, y, z;

        private Immutable(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
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
        public BlockPos minus(int x, int y, int z) {
            return BlockPos.of(this.x - x, this.y - y, this.z - z);
        }
    }

    public static class Mutable extends BlockPos {

        private int x, y, z;
        private BlockPos immutable;

        public Mutable(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public Mutable(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
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
        public BlockPos minus(int x, int y, int z) {
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
            set(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }
}
