package unknowndomain.engine.api.math;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import unknowndomain.engine.api.world.Chunk;

import javax.annotation.Nullable;

public class ChunkPos {
    private int chunkX;
    private int chunkY; //TODO: required review for necessity
    private int chunkZ;

    private Chunk referringChunk;

    public ChunkPos(int cx, int cy, int cz){
        chunkX = cx;
        chunkY = cy;
        chunkZ = cz;
    }

    public static ChunkPos fromBlockPos(BlockPos pos){
        return fromBlockPos(pos, null);
    }

    public static ChunkPos fromBlockPos(BlockPos pos, @Nullable Chunk referring){
        if (referring == null) {
            return new ChunkPos(pos.getX() / Chunk.DEFAULT_X_SIZE,pos.getY() / Chunk.DEFAULT_Y_SIZE,pos.getZ() / Chunk.DEFAULT_Z_SIZE);
        } else {
            return new ChunkPos(pos.getX() / referring.getXSize(),pos.getY() / referring.getYSize(),pos.getZ() / referring.getZSize());
        }
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setReferringChunk(Chunk referringChunk) {
        this.referringChunk = referringChunk;
    }

    public Chunk getReferringChunk() {
        return referringChunk;
    }

    public BlockPos getLowEdge(){
        return getLowEdge(referringChunk);
    }
    public BlockPos getHighEdge(){
        return getHighEdge(referringChunk);
    }
    public BlockPos getLowEdge(@Nullable Chunk referring){
        if(referring == null){
            return new BlockPos(chunkX * Chunk.DEFAULT_X_SIZE, chunkY * Chunk.DEFAULT_Y_SIZE, chunkZ * Chunk.DEFAULT_Z_SIZE);
        }else{
            return new BlockPos(chunkX * referring.getXSize(), chunkY * referring.getYSize(), chunkZ * referring.getZSize());
        }
    }
    public BlockPos getHighEdge(@Nullable Chunk referring){
        if(referring == null){
            return new BlockPos((chunkX + 1) * Chunk.DEFAULT_X_SIZE - 1, (chunkY + 1) * Chunk.DEFAULT_Y_SIZE - 1, (chunkZ + 1) * Chunk.DEFAULT_Z_SIZE - 1);
        }else{
            return new BlockPos((chunkX + 1) * referring.getXSize() - 1, (chunkY + 1) * referring.getYSize() - 1, (chunkZ + 1) * referring.getZSize() - 1);
        }
    }

    public BlockPos getWorldCoordBlock(int x, int y, int z){
        return getWorldCoordBlock(x, y, z, referringChunk);
    }
    public BlockPos getWorldCoordBlock(int x, int y, int z, @Nullable Chunk referring){
        if(referring == null){
            return new BlockPos(chunkX * Chunk.DEFAULT_X_SIZE + x, chunkY * Chunk.DEFAULT_Y_SIZE + y, chunkZ * Chunk.DEFAULT_Z_SIZE + z);
        }else{
            return new BlockPos(chunkX * referring.getXSize() + x, chunkY * referring.getYSize() + y, chunkZ * referring.getZSize() + z);
        }
    }

    public BlockPos getChunkCoordBlock(int x, int y, int z){
        return getChunkCoordBlock(x, y, z, referringChunk);
    }
    public BlockPos getChunkCoordBlock(BlockPos pos){
        return getChunkCoordBlock(pos, referringChunk);
    }
    public BlockPos getChunkCoordBlock(BlockPos pos, @Nullable Chunk referring){
        return getChunkCoordBlock(pos.getX(), pos.getY(), pos.getZ(), referring);
    }
    public BlockPos getChunkCoordBlock(int x, int y, int z, @Nullable Chunk referring){
        if(referring == null){
            return new BlockPos(x - chunkX * Chunk.DEFAULT_X_SIZE, y - chunkY * Chunk.DEFAULT_Y_SIZE, z - chunkZ * Chunk.DEFAULT_Z_SIZE);
        }else{
            return new BlockPos(x - chunkX * referring.getXSize(), y - chunkY * referring.getYSize(), z - chunkZ * referring.getZSize());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof ChunkPos)) {
            return false;
        } else{
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
        return String.format("ChunkPos(%d,%d,%d)", chunkX,chunkY,chunkZ);
    }
}
