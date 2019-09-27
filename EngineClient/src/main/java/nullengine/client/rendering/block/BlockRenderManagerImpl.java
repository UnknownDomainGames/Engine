package nullengine.client.rendering.block;

import nullengine.Platform;
import nullengine.block.Block;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetTypes;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.util.Direction;
import nullengine.world.BlockGetter;

import java.util.HashMap;
import java.util.Map;

public class BlockRenderManagerImpl implements BlockRenderManager {

    private final Map<Block, Asset<BakedModel>> blockModelMap = new HashMap<>();

    public void init() {
        Registries.getBlockRegistry().getValues().forEach(this::registerBlockRenderer);
    }

    private void registerBlockRenderer(Block block) {
        block.getComponent(BlockDisplay.class).ifPresent(blockDisplay -> {
            if (blockDisplay.isVisible()) {
                blockModelMap.put(block, Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, blockDisplay.getModelUrl()));
            }
        });
    }

    @Override
    public boolean canRenderFace(BlockGetter world, BlockPos pos, Block block, Direction direction) {
        var neighborPos = pos.offset(direction);
        var neighborBlock = world.getBlock(neighborPos);
        return canRenderNeighborBlockFace(world, neighborPos, neighborBlock, direction.opposite());
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, Block block, Direction direction) {
        Asset<BakedModel> model = blockModelMap.get(block);
        return model == null || !model.get().isFullFace(direction);
    }

    @Override
    public void generateMesh(Block block, BlockGetter world, BlockPos pos, GLBuffer buffer) {
        Asset<BakedModel> model = blockModelMap.get(block);
        if (model == null) {
            return;
        }

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
        Asset<BakedModel> model = blockModelMap.get(block);
        if (model == null) {
            return;
        }
        buffer.posOffset(-0.5f, -0.5f, -0.5f);
        model.get().putVertexes(buffer, 0);
    }

    public void dispose() {
        blockModelMap.values().forEach(Asset::dispose);
    }
}
