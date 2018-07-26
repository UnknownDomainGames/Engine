package unknowndomain.engine.client.block;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.BoundingBox;
import unknowndomain.engine.api.math.Vector3d;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.api.world.World;

public class Air implements Block{
	private BoundingBox box;
	private World world;
	private BlockPos blockPos;
	
	public Air(World world,BlockPos blockPos) {
		box=new BoundingBox() {
			@Override
			public Vector3d getCentre() {
				return null;
			}

			@Override
			public boolean isCollided(BoundingBox others) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.world=world;
		this.blockPos=blockPos;
	}
	
	@Override
	public DomainedPath getRegistryName() {
		return null;
	}

	@Override
	public Block setRegistryName(DomainedPath location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<Block> getRegistryType() {
		// TODO Auto-generated method stub
		return null;
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
