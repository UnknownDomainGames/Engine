package unknowndomain.engine.api.world;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.ChunkPos;

public interface World {
	
	String getName();
	
	Block getBlock(int x, int y, int z);
	
	Block getBlock(BlockPos pos);
	
	void setBlock(BlockPos pos,Block block);

	Chunk getChunk(int chunkX, int chunkY, int chunkZ);

	Chunk getChunk(ChunkPos pos);
}
