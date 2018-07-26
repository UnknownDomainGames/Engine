package unknowndomain.engine.api.block;

import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.BoundingBox;
import unknowndomain.engine.api.registry.RegistryEntry;

public class BlockBase extends RegistryEntry.Impl<Block> implements Block {

    private int hardness;

    private int antiExplosive;

    protected BoundingBox boundingBox;

    @Override
    public boolean onBlockPlaced(BlockPos pos){
        return true;
    }

    @Override
    public boolean onBlockDestroyed(BlockPos pos, boolean harvested){
        return true;
    }

    @Override
    public BoundingBox getBoundingBox(){
        return boundingBox;
    }

	@Override
	public BlockPos getBlockPos() {
		// TODO Auto-generated method stub
		return null;
	}
}
