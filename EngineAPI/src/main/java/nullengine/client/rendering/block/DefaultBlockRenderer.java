package nullengine.client.rendering.block;

import nullengine.Platform;
import nullengine.block.Block;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetTypes;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.model.block.BlockModel;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.BlockPos;
import nullengine.util.Facing;
import nullengine.world.BlockGetter;

import java.util.Optional;

public class DefaultBlockRenderer implements BlockRenderer {

    private BlockRenderType renderType;
    private Asset<BlockModel> model;

    @Override
    public boolean canRenderFace(BlockGetter world, BlockPos pos, Block block, Facing facing) {
        BlockPos neighborPos = pos.offset(facing);
        Block neighborBlock = world.getBlock(neighborPos);
        Optional<BlockRenderer> neighborBlockRenderer = neighborBlock.getComponent(BlockRenderer.class);
        return neighborBlockRenderer.map(blockRenderer -> blockRenderer.canRenderNeighborBlockFace(world, neighborPos, neighborBlock, facing.opposite())).orElse(true);
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, Block block, Facing facing) {
        return !model.get().getFullFaces()[facing.index];
    }

    @Override
    public void generateMesh(Block block, BlockGetter world, BlockPos pos, GLBuffer buffer) {
        buffer.posOffset(pos.getX(), pos.getY(), pos.getZ());
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        boolean[] cullFaces = new boolean[6];
        for (Facing facing : Facing.values()) {
            mutablePos.set(pos);
            if (!canRenderFace(world, mutablePos, block, facing)) {
                cullFaces[facing.index] = true;
            }
        }

        BlockModel blockModel = this.model.get();
        for (BlockModel.Mesh mesh : blockModel.getMeshes()) {
            if (!checkCullFaces(cullFaces, mesh.cullFaces)) {
                for (BlockModel.Vertex vertex : mesh.vertexes) {
                    buffer.pos(vertex.pos).color(1, 1, 1).uv(vertex.u, vertex.v).normal(vertex.normal).endVertex();
                }
            }
        }
    }

    private boolean checkCullFaces(boolean[] cullFaces, boolean[] meshCullFaces) {
        for (int i = 0; i < 6; i++) {
            if (meshCullFaces[i] && !cullFaces[i])
                return false;
        }
        return true;
    }

    @Override
    public void generateMesh(Block block, GLBuffer buffer) {
        buffer.posOffset(-0.5f, -0.5f, -0.5f);
        BlockModel blockModel = this.model.get();
        for (BlockModel.Mesh mesh : blockModel.getMeshes()) {
            for (BlockModel.Vertex vertex : mesh.vertexes) {
                buffer.pos(vertex.pos).color(1, 1, 1).uv(vertex.u, vertex.v).normal(vertex.normal).endVertex();
            }
        }
    }

    @Override
    public BlockRenderType getRenderType() {
        return renderType;
    }

    public DefaultBlockRenderer setModel(Asset<BlockModel> model) {
        this.model = model;
        return this;
    }

    public DefaultBlockRenderer setModelPath(AssetURL path) {
        this.model = Platform.getEngineClient().getAssetManager().create(AssetTypes.BLOCK_MODEL, path);
        return this;
    }

    public DefaultBlockRenderer setRenderType(BlockRenderType renderType) {
        this.renderType = renderType;
        return this;
    }
}
