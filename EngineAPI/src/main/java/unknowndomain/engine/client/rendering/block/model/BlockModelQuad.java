package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.rendering.texture.TextureAtlasPart;
import unknowndomain.engine.util.Facing;

public class BlockModelQuad {

    public float[] vertexs;
    public Facing facing;
    public TextureAtlasPart textureAtlasPart;

    public BlockModelQuad(float[] vertexs, Facing facing, TextureAtlasPart textureAtlasPart) {
        this.vertexs = vertexs;
        this.facing = facing;
        this.textureAtlasPart = textureAtlasPart;
    }

    public static BlockModelQuad createQuad(float fromX, float fromY, float toX, float toY, float z, Facing facing, TextureAtlasPart textureAtlasPart) {
        switch (facing) {
            case NORTH:
                return new BlockModelQuad(new float[]{fromX, fromY, z, toX, fromY, z, toX, toY, z, fromX, toY, z}, facing, textureAtlasPart);
            case SOUTH:
                return new BlockModelQuad(new float[]{toX, fromY, z, fromX, fromY, z, fromX, toY, z, toX, toY, z}, facing, textureAtlasPart);
            case EAST:
                return new BlockModelQuad(new float[]{z, fromX, toY, z, fromX, fromY, z, toX, fromY, z, toX, toY}, facing, textureAtlasPart);
            case WEST:
                return new BlockModelQuad(new float[]{z, fromX, fromY, z, fromX, toY, z, toX, toY, z, toX, fromY}, facing, textureAtlasPart);
            case UP:
                return new BlockModelQuad(new float[]{fromX, z, toY, toX, z, toY, toX, z, fromY, fromX, z, fromY}, facing, textureAtlasPart);
            case DOWN:
                return new BlockModelQuad(new float[]{toX, z, toY, fromX, z, toY, fromX, z, fromY, toX, z, fromY}, facing, textureAtlasPart);
            default:
                throw new IllegalArgumentException();
        }
    }
}
