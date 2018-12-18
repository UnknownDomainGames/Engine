package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.rendering.texture.Texture;
import unknowndomain.engine.util.Facing;

public class BlockModelQuad {

    public float[] vertexs;
    public Facing facing;
    public Texture texture;

    public BlockModelQuad(float[] vertexs, Facing facing, Texture texture) {
        this.vertexs = vertexs;
        this.facing = facing;
        this.texture = texture;
    }

    public static BlockModelQuad createQuad(float fromX, float fromY, float toX, float toY, float z, Facing facing, Texture texture) {
        switch (facing) {
            case NORTH:
                return new BlockModelQuad(new float[]{fromX, fromY, z, toX, fromY, z, toX, toY, z, fromX, toY, z}, facing, texture);
            case SOUTH:
                return new BlockModelQuad(new float[]{toX, fromY, z, fromX, fromY, z, fromX, toY, z, toX, toY, z}, facing, texture);
            case EAST:
                return new BlockModelQuad(new float[]{z, fromX, toY, z, fromX, fromY, z, toX, fromY, z, toX, toY}, facing, texture);
            case WEST:
                return new BlockModelQuad(new float[]{z, fromX, fromY, z, fromX, toY, z, toX, toY, z, toX, fromY}, facing, texture);
            case TOP:
                return new BlockModelQuad(new float[]{fromX, z, toY, toX, z, toY, toX, z, fromY, fromX, z, fromY}, facing, texture);
            case BOTTOM:
                return new BlockModelQuad(new float[]{toX, z, toY, fromX, z, toY, fromX, z, fromY, toX, z, fromY}, facing, texture);
            default:
                throw new IllegalArgumentException();
        }
    }
}
