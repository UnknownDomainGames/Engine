package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.block.BlockMeshGenerator;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.Math2;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static unknowndomain.engine.client.rendering.block.model.BlockModelQuad.createQuad;

public class BlockModel implements BlockMeshGenerator {

    public Map<Facing, List<BlockModelQuad>> facedModelQuads = new EnumMap<>(Facing.class);

    public BlockModel() {
    }

    public void addQuad(BlockModelQuad modelQuad) {
        facedModelQuads.computeIfAbsent(modelQuad.facing, key -> new ArrayList<>()).add(modelQuad);
    }

    public void addCube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, TextureUV[] textureUVS) {
        addQuad(createQuad(fromX, fromY, toX, toY, toZ, Facing.NORTH, textureUVS[Facing.NORTH.ordinal()]));
        addQuad(createQuad(fromX, fromY, toX, toY, fromZ, Facing.SOUTH, textureUVS[Facing.SOUTH.ordinal()]));
        addQuad(createQuad(fromY, fromZ, toY, toZ, toX, Facing.EAST, textureUVS[Facing.EAST.ordinal()]));
        addQuad(createQuad(fromY, fromZ, toY, toZ, fromX, Facing.WEST, textureUVS[Facing.WEST.ordinal()]));
        addQuad(createQuad(fromX, fromZ, toX, toZ, toY, Facing.UP, textureUVS[Facing.UP.ordinal()]));
        addQuad(createQuad(fromX, fromZ, toX, toZ, fromY, Facing.DOWN, textureUVS[Facing.DOWN.ordinal()]));
    }

    public void addCube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, TextureUV textureUV) {
        addQuad(createQuad(fromX, fromY, toX, toY, toZ, Facing.NORTH, textureUV));
        addQuad(createQuad(fromX, fromY, toX, toY, fromZ, Facing.SOUTH, textureUV));
        addQuad(createQuad(fromY, fromZ, toY, toZ, toX, Facing.EAST, textureUV));
        addQuad(createQuad(fromY, fromZ, toY, toZ, fromX, Facing.WEST, textureUV));
        addQuad(createQuad(fromX, fromZ, toX, toZ, toY, Facing.UP, textureUV));
        addQuad(createQuad(fromX, fromZ, toX, toZ, fromY, Facing.DOWN, textureUV));
    }

    @Override
    public void generate(ClientBlock block, BlockAccessor world, BlockPos pos, BufferBuilder buffer) {
        buffer.posOffset(pos.getX(), pos.getY(), pos.getZ());
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        for (Facing facing : Facing.values()) {
            mutablePos.set(pos);
            if (!block.canRenderFace(world, mutablePos, facing)) {
                continue;
            }

            for (BlockModelQuad modelQuad : facedModelQuads.get(facing)) {
                renderModelQuad(modelQuad, pos, buffer);
            }
        }
    }

    @Override
    public void generate(ClientBlock block, BufferBuilder buffer) {
        for (Facing facing : Facing.values()) {
            for (BlockModelQuad modelQuad : facedModelQuads.get(facing)) {
                renderModelQuad(modelQuad, BlockPos.ZERO, buffer);
            }
        }
    }

    public void renderModelQuad(BlockModelQuad modelQuad, BlockPos pos, BufferBuilder buffer) {
        TextureUV textureUV = modelQuad.textureUV;
        var normal = Math2.calcNormalByVertices(new float[]{
                modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2],
                modelQuad.vertexs[3], modelQuad.vertexs[4], modelQuad.vertexs[5],
                modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8],
        });
        var normal1 = Math2.calcNormalByVertices(new float[]{
                modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2],
                modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8],
                modelQuad.vertexs[9], modelQuad.vertexs[10], modelQuad.vertexs[11],
        });
        buffer.pos(modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2]).color(1, 1, 1).tex(textureUV.getMinU(), textureUV.getMaxV()).normal(normal).endVertex(); // 1
        buffer.pos(modelQuad.vertexs[3], modelQuad.vertexs[4], modelQuad.vertexs[5]).color(1, 1, 1).tex(textureUV.getMaxU(), textureUV.getMaxV()).normal(normal).endVertex(); // 2
        buffer.pos(modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8]).color(1, 1, 1).tex(textureUV.getMaxU(), textureUV.getMinV()).normal(normal).endVertex(); // 3

        buffer.pos(modelQuad.vertexs[0], modelQuad.vertexs[1], modelQuad.vertexs[2]).color(1, 1, 1).tex(textureUV.getMinU(), textureUV.getMaxV()).normal(normal1).endVertex(); // 1
        buffer.pos(modelQuad.vertexs[6], modelQuad.vertexs[7], modelQuad.vertexs[8]).color(1, 1, 1).tex(textureUV.getMaxU(), textureUV.getMinV()).normal(normal1).endVertex(); // 3
        buffer.pos(modelQuad.vertexs[9], modelQuad.vertexs[10], modelQuad.vertexs[11]).color(1, 1, 1).tex(textureUV.getMinU(), textureUV.getMinV()).normal(normal1).endVertex(); // 4
    }

    @Override
    public void dispose() {

    }
}
