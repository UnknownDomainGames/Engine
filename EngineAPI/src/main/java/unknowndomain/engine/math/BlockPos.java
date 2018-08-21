package unknowndomain.engine.math;

import org.joml.Vector3f;
import unknowndomain.engine.world.Chunk;

public final class BlockPos {
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);

    private final int x, y, z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public BlockPos add(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    public BlockPos minus(int x, int y, int z) {
        return new BlockPos(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPos))
            return false;

        BlockPos others = (BlockPos) obj;

        return x == others.x && y == others.y && z == others.z;
    }

    @Override
    public int hashCode() {
        return (x * 31 + y) * 31 + z;
    }

    @Override
    public String toString() {
        return String.format("BlockPos(%d,%d,%d)", x, y, z);
    }

    public ChunkPos toChunk() {
        return ChunkPos.fromBlockPos(this);
    }

    public int sqDistanceBetween(BlockPos another) {
        int x = this.x - another.x;
        int y = this.y - another.y;
        int z = this.z - another.z;
        return x * x + y * y + z * z;
    }

    public static BlockPos of(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    public static BlockPos of(Vector3f vector3f) {
        return new BlockPos((int) Math.floor(vector3f.x), (int) Math.floor(vector3f.y), (int) Math.floor(vector3f.z));
    }

    public static boolean inSameChunk(BlockPos a, BlockPos b) {
        return ((a.x >> 4) == (b.x >> 4)) && ((a.y >> 4) == (b.y >> 4)) && ((a.z >> 4) == (b.z >> 4));
    }

//    public BlockPos pack() {
//        return new BlockPos(x & 16, y & 16, z & 16);
//    }

    public ChunkPos toChunk(Chunk chunk) {
        return ChunkPos.fromBlockPos(this, chunk);
    }

    public int pack() {
        return ((x & 0xF) << 8) | ((y & 0xF) << 4) | (z & 0xF);
    }
}
