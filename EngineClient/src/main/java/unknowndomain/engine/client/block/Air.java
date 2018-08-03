package unknowndomain.engine.client.block;

import unknowndomain.engine.api.block.BlockBase;
import unknowndomain.engine.api.math.AxisAlignedBB;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.BoundingBox;
import unknowndomain.engine.api.world.World;

public class Air extends BlockBase {
	private BoundingBox box;
	private World world;
	private BlockPos blockPos;
	
	public Air(World world,BlockPos blockPos) {
		box=new AxisAlignedBB(0,0,0,0,0,0);
		this.world=world;
		this.blockPos=blockPos;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return box;
	}

	@Override
	public boolean onBlockPlaced(BlockPos pos) {
		return false;
	}

	@Override
	public boolean onBlockDestroyed(BlockPos pos, boolean harvested) {
		return false;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.blockPos;
	}

}
