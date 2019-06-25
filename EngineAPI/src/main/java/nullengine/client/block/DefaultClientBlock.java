package nullengine.client.block;

import nullengine.block.Block;
import nullengine.client.asset.AssetPath;
import nullengine.client.asset.model.voxel.VoxelMeshGenerator;
import nullengine.client.rendering.block.BlockMeshGenerator;
import nullengine.client.rendering.block.BlockRenderType;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.registry.RegistryEntry;
import nullengine.util.Facing;
import nullengine.world.BlockAccessor;

public class DefaultClientBlock extends RegistryEntry.Impl<ClientBlock> implements ClientBlock {

    private final Block block;

    private BlockMeshGenerator renderer;
    private BlockRenderType renderType;

    public DefaultClientBlock(Block block) {
        this.block = block;
        registerName(block.getRegisterName());
    }

    @Override
    public final Block getBlock() {
        return block;
    }

    @Override
    public boolean canRenderFace(BlockAccessor world, BlockPos pos, Facing facing) {
        BlockPos neighborPos = pos.offset(facing);
        ClientBlock neighborBlock = Registries.getClientBlockRegistry().getValue(world.getBlockId(neighborPos));
        return neighborBlock.canRenderNeighborBlockFace(world, neighborPos, facing.opposite());
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing) {
        return false;
    }

    @Override
    public BlockMeshGenerator getRenderer() {
        return renderer;
    }

    @Override
    public BlockRenderType getRenderType() {
        return renderType;
    }

    public DefaultClientBlock setRenderer(AssetPath path) {
        this.renderer = VoxelMeshGenerator.create(path);
        return this;
    }

    public DefaultClientBlock setRenderer(BlockMeshGenerator renderer) {
        this.renderer = renderer;
        return this;
    }

    public DefaultClientBlock setRenderType(BlockRenderType renderType) {
        this.renderType = renderType;
        return this;
    }
}
