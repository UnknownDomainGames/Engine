package unknowndomain.engine.world;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.ChunkPos;
import unknowndomain.engine.api.world.Chunk;
import unknowndomain.engine.api.world.World;

import java.util.HashMap;
import java.util.Map;

public class SimpleWorld implements World {

    private String worldName;

    private Map<ChunkPos, SimpleChunk> chunkMap;

    public SimpleWorld(String name){
        worldName = name;
        chunkMap = new HashMap<>();
    }

    @Override
    public String getName() {
        return worldName;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return getBlock(new BlockPos(x,y,z));
    }

    @Override
    public Block getBlock(BlockPos pos) {
        ChunkPos chunkPos = ChunkPos.fromBlockPos(pos);
        return getChunk(chunkPos).getBlock(chunkPos.getChunkCoordBlock(pos));
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return getChunk(new ChunkPos(chunkX, chunkY, chunkZ));
    }

    @Override
    public Chunk getChunk(ChunkPos pos) {
        return chunkMap.get(pos);
    }
}
