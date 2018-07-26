package unknowndomain.engine.client.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.ChunkPos;
import unknowndomain.engine.api.world.Chunk;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.block.Air;

public class FlatWorld implements World{
	private String name="FlatWorld";
	private HashMap<BlockPos,Block> blockData=new HashMap<>();
	
	public FlatWorld(String worldName) {
		this.name=worldName;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		BlockPos blockPos=new BlockPos(x,y,z);
		return getBlock(blockPos);
	}

	@Override
	public Block getBlock(BlockPos pos) {
		Block block=blockData.get(pos);
		if(block==null) {
			block=new Air(this,pos);
			blockData.put(pos, block);
		}
		return block;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chunk getChunk(ChunkPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBlock(BlockPos pos, Block block) {
		blockData.put(pos, block);
		
	}

}
