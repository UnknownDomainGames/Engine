package nullengine.client.rendering.model.voxel.item;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.DisplayType;
import nullengine.client.rendering.model.voxel.Model;
import nullengine.client.rendering.texture.TextureAtlasPart;
import nullengine.client.rendering.texture.TextureBuffer;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.Math2;
import nullengine.math.Transformation;
import nullengine.util.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static nullengine.client.rendering.model.voxel.ModelLoadUtils.fillTransformationArray;

public class ItemGenerateModel implements Model {

    private static final float positivePixel = 1f / 32;
    private static final float negativePixel = -1f / 32;

    transient AssetURL url;
    AssetURL texture;
    Transformation[] transformations;

    @Override
    public BakedModel bake(Function<AssetURL, TextureAtlasPart> textureGetter) {
        var texture = textureGetter.apply(this.texture);
        var textureBuffer = texture.getData();
        var u = texture.getMaxU() - texture.getMinU();
        var v = texture.getMaxV() - texture.getMinV();
        List<float[]> vertexes = new ArrayList<>();
        var pixelX = 1f / textureBuffer.getWidth();
        var pixelY = 1f / textureBuffer.getHeight();
        for (int x = 0; x < textureBuffer.getWidth(); x++) {
            for (int y = 0; y < textureBuffer.getHeight(); y++) {
                if ((textureBuffer.getPixel(x, y) & 0xff) == 0) {
                    continue;
                }
                var pixelMaxX = .5f - x * pixelX;
                var pixelMaxY = .5f - y * pixelY;
                var pixelMinX = pixelMaxX - pixelX;
                var pixelMinY = pixelMaxY - pixelY;
                var pixelMinU = u * x * pixelX + texture.getMinU();
                var pixelMinV = v * y * pixelX + texture.getMinV();
                var pixelMaxU = u * pixelX + pixelMinU;
                var pixelMaxV = v * pixelY + pixelMinV;
                if (getAlpha(textureBuffer, x - 1, y) != 0xff) {
                    bakeEast(vertexes, pixelMinY, pixelMaxY, pixelMaxX, pixelMinU, pixelMinV, pixelMaxU, pixelMaxV);
                }

                if (getAlpha(textureBuffer, x + 1, y) != 0xff) {
                    bakeWest(vertexes, pixelMinY, pixelMaxY, pixelMinX, pixelMinU, pixelMinV, pixelMaxU, pixelMaxV);
                }

                if (getAlpha(textureBuffer, x, y - 1) != 0xff) {
                    bakeUp(vertexes, pixelMinX, pixelMaxX, pixelMaxY, pixelMinU, pixelMinV, pixelMaxU, pixelMaxV);
                }

                if (getAlpha(textureBuffer, x, y + 1) != 0xff) {
                    bakeDown(vertexes, pixelMinX, pixelMaxX, pixelMinY, pixelMinU, pixelMinV, pixelMaxU, pixelMaxV);
                }
            }
        }
        bakeSouth(vertexes, texture);
        bakeNorth(vertexes, texture);
        fillTransformationArray(transformations);
        return new BakedModel(vertexes, transformations);
    }

    private int getAlpha(TextureBuffer buffer, int pixelX, int pixelY) {
        if (pixelX < 0 || pixelX >= buffer.getWidth()) return 0;
        if (pixelY < 0 || pixelY >= buffer.getHeight()) return 0;
        return buffer.getPixel(pixelX, pixelY) & 0xff;
    }

    private void bakeNorth(List<float[]> vertexes, TextureAtlasPart texture) {
        bakeQuad(vertexes, new float[]{
                        .5f, -.5f, negativePixel,
                        -.5f, -.5f, negativePixel,
                        -.5f, .5f, negativePixel,
                        .5f, .5f, negativePixel},
                texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV());
    }

    private void bakeSouth(List<float[]> vertexes, TextureAtlasPart texture) {
        bakeQuad(vertexes, new float[]{
                        -.5f, -.5f, positivePixel,
                        .5f, -.5f, positivePixel,
                        .5f, .5f, positivePixel,
                        -.5f, .5f, positivePixel},
                texture.getMaxU(), texture.getMinV(), texture.getMinU(), texture.getMaxV());
    }

    private void bakeEast(List<float[]> vertexes, float y1, float y2, float x, float minU, float minV, float maxU, float maxV) {
        bakeQuad(vertexes, new float[]{
                        x, y1, positivePixel,
                        x, y1, negativePixel,
                        x, y2, negativePixel,
                        x, y2, positivePixel},
                minU, minV, maxU, maxV);
    }

    private void bakeWest(List<float[]> vertexes, float y1, float y2, float x, float minU, float minV, float maxU, float maxV) {
        bakeQuad(vertexes, new float[]{
                        x, y1, negativePixel,
                        x, y1, positivePixel,
                        x, y2, positivePixel,
                        x, y2, negativePixel},
                minU, minV, maxU, maxV);
    }

    private void bakeUp(List<float[]> vertexes, float x1, float x2, float y, float minU, float minV, float maxU, float maxV) {
        bakeQuad(vertexes, new float[]{
                        x1, y, positivePixel,
                        x2, y, positivePixel,
                        x2, y, negativePixel,
                        x1, y, negativePixel},
                minU, minV, maxU, maxV);
    }

    private void bakeDown(List<float[]> vertexes, float x1, float x2, float y, float minU, float minV, float maxU, float maxV) {
        bakeQuad(vertexes, new float[]{
                        x2, y, positivePixel,
                        x1, y, positivePixel,
                        x1, y, negativePixel,
                        x2, y, negativePixel},
                minU, minV, maxU, maxV);
    }

    private void bakeQuad(List<float[]> vertexes, float[] positions, float minU, float minV, float maxU, float maxV) {
        var normal = Math2.calcNormalByVertices(positions);
        var normal1 = Math2.calcNormalByVertices(positions);

        vertexes.add(new float[]{positions[0], positions[1], positions[2], minU, maxV, normal.x(), normal.y(), normal.z()}); // 1
        vertexes.add(new float[]{positions[3], positions[4], positions[5], maxU, maxV, normal.x(), normal.y(), normal.z()}); // 2
        vertexes.add(new float[]{positions[6], positions[7], positions[8], maxU, minV, normal.x(), normal.y(), normal.z()}); // 3

        vertexes.add(new float[]{positions[0], positions[1], positions[2], minU, maxV, normal.x(), normal.y(), normal.z()}); // 1
        vertexes.add(new float[]{positions[6], positions[7], positions[8], maxU, minV, normal.x(), normal.y(), normal.z()}); // 3
        vertexes.add(new float[]{positions[9], positions[10], positions[11], minU, minV, normal1.x(), normal1.y(), normal1.z()}); // 4
    }

    @Override
    public Collection<AssetURL> getTextures() {
        return List.of(texture);
    }

    private static final class BakedModel implements nullengine.client.rendering.model.BakedModel {

        private final List<float[]> vertexes;
        private final Transformation[] transformations;

        private BakedModel(List<float[]> vertexes, Transformation[] transformations) {
            this.vertexes = vertexes;
            this.transformations = transformations;
        }

        @Override
        public void putVertexes(GLBuffer buffer, int coveredFace) {
            for (var vertex : this.vertexes) {
                buffer.pos(vertex, 0).color(1, 1, 1, 1).uv(vertex, 3).normal(vertex, 5).endVertex();
            }
        }

        @Override
        public boolean isFullFace(Direction direction) {
            return false;
        }

        @Override
        public Transformation getTransformation(DisplayType type) {
            return transformations[type.ordinal()];
        }
    }
}
