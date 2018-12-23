package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.util.Facing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static unknowndomain.engine.client.rendering.block.model.BlockModelQuad.createQuad;

public class BlockModel {

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
        addQuad(createQuad(fromX, fromZ, toX, toZ, toY, Facing.TOP, textureUVS[Facing.TOP.ordinal()]));
        addQuad(createQuad(fromX, fromZ, toX, toZ, fromY, Facing.BOTTOM, textureUVS[Facing.BOTTOM.ordinal()]));
    }

    public void addCube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, TextureUV textureUV) {
        addQuad(createQuad(fromX, fromY, toX, toY, toZ, Facing.NORTH, textureUV));
        addQuad(createQuad(fromX, fromY, toX, toY, fromZ, Facing.SOUTH, textureUV));
        addQuad(createQuad(fromY, fromZ, toY, toZ, toX, Facing.EAST, textureUV));
        addQuad(createQuad(fromY, fromZ, toY, toZ, fromX, Facing.WEST, textureUV));
        addQuad(createQuad(fromX, fromZ, toX, toZ, toY, Facing.TOP, textureUV));
        addQuad(createQuad(fromX, fromZ, toX, toZ, fromY, Facing.BOTTOM, textureUV));
    }
}
