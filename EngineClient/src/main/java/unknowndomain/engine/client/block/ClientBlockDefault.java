package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

import java.util.HashMap;
import java.util.Map;

public class ClientBlockDefault extends RegistryEntry.Impl<ClientBlock> implements ClientBlock {

    @Deprecated
    public static final Map<Block, BlockModel> blockModelMap = new HashMap<>();

    private final Block block;

    @Deprecated
    private BlockModel model;
//    private final BlockModel model;

//    public ClientBlockDefault(Block block, BlockModel model) {
//        this.block = block;
//        this.model = model;
//        localName(block.getLocalName());
//    }

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
    public boolean isRenderable() {
        return true;
    }

    @Override
    public boolean canRenderFace(BlockAccessor world, BlockPos pos, Facing facing) {
        BlockPos neighborPos = facing.offset(pos);
        ClientBlock neighborBlock = UnknownDomain.getGame().getClientContext().getClientBlockRegistry().getValue(world.getBlockId(neighborPos));
        return neighborBlock.canRenderNeighborBlockFace(world, neighborPos, facing.opposite());
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing) {
        return false;
    }

    @Override
    public BlockModel getModel() {
        if (model == null) {
            model = blockModelMap.get(block);
        }
        return model;
    }
}
