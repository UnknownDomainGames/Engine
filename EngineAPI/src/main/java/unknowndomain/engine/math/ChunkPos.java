package unknowndomain.engine.math;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import unknowndomain.engine.world.chunk.Chunk;

public class ChunkPos {
    private final int chunkX;
    private final int chunkY; //TODO: required review for necessity
    private final int chunkZ;

    public ChunkPos(int cx, int cy, int cz) {
        chunkX = cx;
        chunkY = cy;
        chunkZ = cz;
    }

    public static ChunkPos fromBlockPos(BlockPos pos) {
        return new ChunkPos(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    public int getX() {
        return chunkX;
    }

    public int getY() {
        return chunkY;
    }

    public int getZ() {
        return chunkZ;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public BlockPos getLowEdge() {
        return new BlockPos(chunkX * Chunk.DEFAULT_X_SIZE, chunkY * Chunk.DEFAULT_Y_SIZE, chunkZ * Chunk.DEFAULT_Z_SIZE);
    }

    public BlockPos getHighEdge() {
        return new BlockPos((chunkX + 1) * Chunk.DEFAULT_X_SIZE - 1, (chunkY + 1) * Chunk.DEFAULT_Y_SIZE - 1, (chunkZ + 1) * Chunk.DEFAULT_Z_SIZE - 1);
    }

    public BlockPos getWorldCoordBlock(int x, int y, int z) {
        return new BlockPos(chunkX * Chunk.DEFAULT_X_SIZE + x, chunkY * Chunk.DEFAULT_Y_SIZE + y, chunkZ * Chunk.DEFAULT_Z_SIZE + z);
    }

    public BlockPos getChunkCoordBlock(BlockPos pos) {
        return getChunkCoordBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos getChunkCoordBlock(int x, int y, int z) {
        return new BlockPos(x - chunkX * Chunk.DEFAULT_X_SIZE, y - chunkY * Chunk.DEFAULT_Y_SIZE, z - chunkZ * Chunk.DEFAULT_Z_SIZE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ChunkPos)) {
            return false;
        } else {
            ChunkPos other = (ChunkPos) obj;
            return other.chunkX == chunkX && other.chunkY == chunkY && other.chunkZ == chunkZ;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(chunkX).append(chunkY).append(chunkZ).build();
    }

    @Override
    public String toString() {
        return String.format("ChunkPos(%d,%d,%d)", chunkX, chunkY, chunkZ);
    }

    public int compact() {
        return getChunkX() << 16 | (getChunkZ() & 0xFFFF);
    }

    public long fat() {
        return ((long) getChunkX() << 32) | getChunkZ();
    }
}
