package unknowndomain.engine.math;

import com.google.common.base.MoreObjects;
import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.util.Facing;

import static unknowndomain.engine.world.chunk.ChunkConstants.*;

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
        return ((a.getX() >> BITS_X) == (b.getX() >> BITS_X)) && ((a.getY() >> BITS_Y) == (b.getY() >> BITS_Y)) && ((a.getZ() >> BITS_Z) == (b.getZ() >> BITS_Z));
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

    public abstract BlockPos toImmutable();

    public abstract BlockPos add(int x, int y, int z);

    public abstract BlockPos minus(int x, int y, int z);

    public int squareDistanceTo(BlockPos another) {
        int x = this.getX() - another.getX();
        int y = this.getY() - another.getY();
        int z = this.getZ() - another.getZ();
        return x * x + y * y + z * z;
    }

    public BlockPos offset(Facing facing) {
        return add(facing.offsetX, facing.offsetY, facing.offsetZ);
    }

    public BlockPos offset(Facing facing, int offset) {
        return add(facing.offsetX * offset, facing.offsetY * offset, facing.offsetZ * offset);
    }

    public BlockPos north() {
        return offset(Facing.NORTH);
    }

    public BlockPos north(int offset) {
        return offset(Facing.NORTH, offset);
    }

    public BlockPos south() {
        return offset(Facing.SOUTH);
    }

    public BlockPos south(int offset) {
        return offset(Facing.SOUTH, offset);
    }

    public BlockPos east() {
        return offset(Facing.EAST);
    }

    public BlockPos east(int offset) {
        return offset(Facing.EAST, offset);
    }

    public BlockPos up() {
        return offset(Facing.UP);
    }

    public BlockPos up(int offset) {
        return offset(Facing.UP, offset);
    }

    public BlockPos down() {
        return offset(Facing.DOWN);
    }

    public BlockPos down(int offset) {
        return offset(Facing.DOWN, offset);
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

        public int x, y, z;
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
