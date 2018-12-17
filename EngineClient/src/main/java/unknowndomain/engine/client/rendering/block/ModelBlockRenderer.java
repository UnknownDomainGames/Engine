package unknowndomain.engine.client.rendering.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.client.rendering.block.model.BlockModelQuad;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.util.ChunkCache;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelBlockRenderer implements BlockRenderer {

    public final Map<Block, BlockModel> blockModelMap = new HashMap<>();

    @Override
    public void render(Block block, ChunkCache chunkCache, BlockPos pos, BufferBuilder buffer) {
        BlockModel blockModel = blockModelMap.get(block);
        if (blockModel == null) {
            return;
        }
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        for (Facing facing : Facing.values()) {
            mutablePos.set(pos);
            facing.offset(pos);
            Block facingBlock = chunkCache.getBlock(pos);
            if (facingBlock == BlockAir.AIR) {
                continue;
            }

            for (BlockModelQuad modelQuad : blockModel.facedModelQuads.computeIfAbsent(facing, key -> Collections.emptyList())) {
                renderModelQuad(modelQuad, pos, buffer);
            }
        }
    }

    public void renderModel(BlockModel model, BlockPos pos, BufferBuilder buffer) {
        for (Facing facing : Facing.values()) {
            facing.offset(pos);

            for (BlockModelQuad modelQuad : model.facedModelQuads.computeIfAbsent(facing, key -> Collections.emptyList())) {
                renderModelQuad(modelQuad, pos, buffer);
            }
        }
    }

    public void renderModelQuad(BlockModelQuad modelQuad, BlockPos pos, BufferBuilder buffer) {
        buffer.pos(modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2]).tex(0f, 1f).endVertex(); // 1
        buffer.pos(modelQuad.vertexs[3], modelQuad.vertexs[4], modelQuad.vertexs[5]).tex(1f, 1f).endVertex(); // 2
        buffer.pos(modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8]).tex(1f, 0f).endVertex(); // 3

        buffer.pos(modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2]).tex(0f, 1f).endVertex(); // 1
        buffer.pos(modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8]).tex(1f, 0f).endVertex(); // 3
        buffer.pos(modelQuad.vertexs[9], modelQuad.vertexs[10], modelQuad.vertexs[11]).tex(0f, 0f).endVertex(); // 4
    }

    @Override
    public void dispose() {

    }
}
