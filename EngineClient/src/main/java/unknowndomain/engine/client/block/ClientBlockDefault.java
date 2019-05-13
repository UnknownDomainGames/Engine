package unknowndomain.engine.client.block;

import unknowndomain.engine.Platform;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.rendering.block.BlockMeshGenerator;
import unknowndomain.engine.client.rendering.block.BlockRenderType;
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
    public static final Map<Block, BlockMeshGenerator> blockRendererMap = new HashMap<>();

    private final Block block;

    private BlockMeshGenerator renderer;
    private BlockRenderType renderType;

    @Deprecated
    public ClientBlockDefault(Block block) {
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
        // FIXME:
        ClientBlock neighborBlock = Platform.getEngineClient().getCurrentGame().getRegistryManager().getRegistry(ClientBlock.class).getValue(world.getBlockId(neighborPos));
        return neighborBlock.canRenderNeighborBlockFace(world, neighborPos, facing.opposite());
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing) {
        return false;
    }

    @Override
    public BlockMeshGenerator getRenderer() {
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
