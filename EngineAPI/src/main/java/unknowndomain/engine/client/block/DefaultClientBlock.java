package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.block.BlockMeshGenerator;
import unknowndomain.engine.client.rendering.block.BlockRenderType;
import unknowndomain.engine.client.rendering.model.voxel.VoxelMeshGenerator;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Registries;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

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
