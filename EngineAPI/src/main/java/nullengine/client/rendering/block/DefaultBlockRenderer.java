package nullengine.client.rendering.block;

import nullengine.Platform;
import nullengine.block.Block;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetTypes;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.BlockPos;
import nullengine.util.Direction;
import nullengine.world.BlockGetter;

public class DefaultBlockRenderer implements BlockRenderer {

    private BlockRenderType renderType;
    private Asset<BakedModel> model;

    @Override
    public boolean canRenderFace(BlockGetter world, BlockPos pos, Block block, Direction direction) {
        var neighborPos = pos.offset(direction);
        var neighborBlock = world.getBlock(neighborPos);
        var neighborBlockRenderer = neighborBlock.getComponent(BlockRenderer.class);
        return neighborBlockRenderer
                .map(blockRenderer -> blockRenderer.canRenderNeighborBlockFace(world, neighborPos, neighborBlock, direction.opposite()))
                .orElse(true);
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, Block block, Direction direction) {
        return !model.get().isFullFace(direction);
    }

    @Override
    public void generateMesh(Block block, BlockGetter world, BlockPos pos, GLBuffer buffer) {
        buffer.posOffset(pos.x(), pos.y(), pos.z());
        var mutablePos = new BlockPos.Mutable(pos);
        byte coveredFace = 0;
        for (var direction : Direction.values()) {
            mutablePos.set(pos);
            if (!canRenderFace(world, mutablePos, block, direction)) {
                coveredFace |= 1 << direction.index;
            }
        }

        model.get().putVertexes(buffer, coveredFace);
    }

    @Override
    public void generateMesh(Block block, GLBuffer buffer) {
        buffer.posOffset(-0.5f, -0.5f, -0.5f);
        model.get().putVertexes(buffer, 0);
    }

    @Override
    public BlockRenderType getRenderType() {
        return renderType;
    }

    public DefaultBlockRenderer setModelPath(AssetURL path) {
        this.model = Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, path);
        return this;
    }

    public DefaultBlockRenderer setRenderType(BlockRenderType renderType) {
        this.renderType = renderType;
        return this;
    }
}
