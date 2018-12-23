package unknowndomain.engine.client.rendering.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.client.rendering.block.model.BlockModelQuad;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.ChunkCache;
import unknowndomain.engine.util.Facing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelBlockRenderer implements BlockRenderer {

    // TODO:
    public static final Map<Block, BlockModel> blockModelMap = new HashMap<>();

    @Override
    public void render(Block block, ChunkCache chunkCache, BlockPos pos, BufferBuilder buffer) {
        BlockModel blockModel = blockModelMap.get(block);
        if (blockModel == null) {
            return;
        }
        buffer.posOffest(pos.getX(), pos.getY(), pos.getZ());
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        for (Facing facing : Facing.values()) {
            mutablePos.set(pos);
            facing.offset(mutablePos);
            Block facingBlock = chunkCache.getBlock(mutablePos);
            if (facingBlock != chunkCache.getWorld().getGame().getContext().getBlockAir()) {
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
        TextureUV textureUV = modelQuad.textureUV;
        buffer.pos(modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2]).color(1, 1, 1).tex(textureUV.getMinU(), textureUV.getMaxV()).endVertex(); // 1
        buffer.pos(modelQuad.vertexs[3], modelQuad.vertexs[4], modelQuad.vertexs[5]).color(1, 1, 1).tex(textureUV.getMaxU(), textureUV.getMaxV()).endVertex(); // 2
        buffer.pos(modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8]).color(1, 1, 1).tex(textureUV.getMaxU(), textureUV.getMinV()).endVertex(); // 3

        buffer.pos(modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2]).color(1, 1, 1).tex(textureUV.getMinU(), textureUV.getMaxV()).endVertex(); // 1
        buffer.pos(modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8]).color(1, 1, 1).tex(textureUV.getMaxU(), textureUV.getMinV()).endVertex(); // 3
        buffer.pos(modelQuad.vertexs[9], modelQuad.vertexs[10], modelQuad.vertexs[11]).color(1, 1, 1).tex(textureUV.getMinU(), textureUV.getMinV()).endVertex(); // 4
    }

    @Override
    public void dispose() {

    }
}
