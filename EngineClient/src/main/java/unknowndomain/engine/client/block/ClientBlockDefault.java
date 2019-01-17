package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.rendering.block.BlockRenderType;
import unknowndomain.engine.client.rendering.block.BlockRenderer;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

import java.util.HashMap;
import java.util.Map;

public class ClientBlockDefault extends RegistryEntry.Impl<ClientBlock> implements ClientBlock {

    // TODO: Better renderer init.
    @Deprecated
    public static final Map<Block, BlockModel> blockRendererMap = new HashMap<>();

    private final Block block;

    private BlockRenderer renderer;
    private BlockRenderType renderType;

    @Deprecated
    public ClientBlockDefault(Block block) {
        this.block = block;
        localName(block.getLocalName());
    }

    @Override
    public final Block getBlock() {
        return block;
    }

    @Override
    public boolean canRenderFace(BlockAccessor world, BlockPos pos, Facing facing) {
        BlockPos neighborPos = pos.offset(facing);
        ClientBlock neighborBlock = UnknownDomain.getGame().getClientContext().getClientBlockRegistry().getValue(world.getBlockId(neighborPos));
        return neighborBlock.canRenderNeighborBlockFace(world, neighborPos, facing.opposite());
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing) {
        return false;
    }

    @Override
    public BlockRenderer getRenderer() {
        if (renderer == null) {
            renderer = blockRendererMap.get(block);
        }
        return renderer;
    }

    @Override
    public BlockRenderType getRenderType() {
        return renderType;
    }
}
