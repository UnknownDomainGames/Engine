package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.rendering.texture.GLTexturePart;
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

    public void addCube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, GLTexturePart[] textures) {
        addQuad(createQuad(fromX, fromY, toX, toY, toZ, Facing.NORTH, textures[Facing.NORTH.ordinal()]));
        addQuad(createQuad(fromX, fromY, toX, toY, fromZ, Facing.SOUTH, textures[Facing.SOUTH.ordinal()]));
        addQuad(createQuad(fromY, fromZ, toY, toZ, toX, Facing.EAST, textures[Facing.EAST.ordinal()]));
        addQuad(createQuad(fromY, fromZ, toY, toZ, fromX, Facing.WEST, textures[Facing.WEST.ordinal()]));
        addQuad(createQuad(fromX, fromZ, toX, toZ, toY, Facing.TOP, textures[Facing.TOP.ordinal()]));
        addQuad(createQuad(fromX, fromZ, toX, toZ, fromY, Facing.BOTTOM, textures[Facing.BOTTOM.ordinal()]));
    }

    public void addCube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, GLTexturePart texture) {
        addQuad(createQuad(fromX, fromY, toX, toY, toZ, Facing.NORTH, texture));
        addQuad(createQuad(fromX, fromY, toX, toY, fromZ, Facing.SOUTH, texture));
        addQuad(createQuad(fromY, fromZ, toY, toZ, toX, Facing.EAST, texture));
        addQuad(createQuad(fromY, fromZ, toY, toZ, fromX, Facing.WEST, texture));
        addQuad(createQuad(fromX, fromZ, toX, toZ, toY, Facing.TOP, texture));
        addQuad(createQuad(fromX, fromZ, toX, toZ, fromY, Facing.BOTTOM, texture));
    }
}
