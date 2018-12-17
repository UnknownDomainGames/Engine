package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.rendering.texture.GLTexturePart;
import unknowndomain.engine.util.Facing;

public class BlockModelQuad {

    public float[] vertexs;
    public Facing facing;
    public GLTexturePart texture;

    public BlockModelQuad(float[] vertexs, Facing facing, GLTexturePart texture) {
        this.vertexs = vertexs;
        this.facing = facing;
        this.texture = texture;
    }

    public static BlockModelQuad createQuad(float fromX, float fromY, float toX, float toY, float z, Facing facing, GLTexturePart texture) {
        switch (facing) {
            case NORTH:
                return new BlockModelQuad(new float[]{fromX, fromY, z, toX, fromY, z, toX, toY, z, fromX, toY, z}, facing, texture);
            case SOUTH:
                return new BlockModelQuad(new float[]{fromX, fromY, z, fromX, toY, z, toX, toY, z, toX, fromY, z}, facing, texture);
            case EAST:
                return new BlockModelQuad(new float[]{z, fromX, fromY, z, toX, fromY, z, toX, toY, z, fromX, toY}, facing, texture);
            case WEST:
                return new BlockModelQuad(new float[]{z, fromX, fromY, z, fromX, toY, z, toX, toY, z, toX, fromY}, facing, texture);
            case TOP:
                return new BlockModelQuad(new float[]{fromX, z, fromY, fromX, z, toY, toX, z, toY, toX, z, fromY}, facing, texture);
            case BOTTOM:
                return new BlockModelQuad(new float[]{fromX, z, fromY, toX, z, fromY, toX, z, toY, fromX, z, toY}, facing, texture);
            default:
                throw new IllegalArgumentException();
        }
    }
}
