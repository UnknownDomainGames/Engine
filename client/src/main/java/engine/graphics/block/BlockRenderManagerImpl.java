package engine.graphics.block;

import engine.Platform;
import engine.block.Block;
import engine.block.state.BlockState;
import engine.client.asset.Asset;
import engine.client.asset.AssetTypes;
import engine.graphics.model.BakedModel;
import engine.graphics.vertex.VertexDataBuf;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.util.Direction;
import engine.world.BlockGetter;

import java.util.HashMap;
import java.util.Map;

public final class BlockRenderManagerImpl implements BlockRenderManager {

    private final Map<BlockState, Asset<BakedModel>> blockModelMap = new HashMap<>();

    public void init() {
        Registries.getBlockRegistry().getValues().forEach(this::registerBlockRenderer);
    }

    private void registerBlockRenderer(Block block) {
        block.getComponent(BlockDisplay.class).ifPresent(blockDisplay -> {
            if (blockDisplay.isVisible()) {
                for (BlockState state : block.getStateManager().getStates()) {
                    blockModelMap.put(state, Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, blockDisplay.getVariantModelUrls().getOrDefault(state, blockDisplay.getModelUrl())));
                }
            }
        });
    }

    @Override
    public boolean canRenderFace(BlockGetter world, BlockPos pos, BlockState block, Direction direction) {
        var neighborPos = pos.offset(direction);
        var neighborBlock = world.getBlock(neighborPos);
        return canRenderNeighborBlockFace(world, neighborPos, neighborBlock, direction.opposite());
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, BlockState block, Direction direction) {
        Asset<BakedModel> model = blockModelMap.get(block);
        return model == null || !model.get().isFullFace(direction);
    }

    @Override
    public void generateMesh(BlockState block, BlockGetter world, BlockPos pos, VertexDataBuf buffer) {
        Asset<BakedModel> model = blockModelMap.get(block);
        if (model == null) {
            return;
        }

        buffer.setTranslation(pos.x(), pos.y(), pos.z());
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
    public void generateMesh(BlockState block, VertexDataBuf buffer) {
        Asset<BakedModel> model = blockModelMap.get(block);
        if (model == null) {
            return;
        }
        buffer.setTranslation(-0.5f, -0.5f, -0.5f);
        model.get().putVertexes(buffer, 0);
    }

    public void dispose() {
        blockModelMap.values().forEach(Asset::dispose);
    }
}
