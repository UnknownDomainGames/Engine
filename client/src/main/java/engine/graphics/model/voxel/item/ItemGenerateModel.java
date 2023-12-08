package engine.graphics.model.voxel.item;

import engine.client.asset.AssetURL;
import engine.graphics.image.BufferedImage;
import engine.graphics.math.RenderingMath;
import engine.graphics.math.Transform;
import engine.graphics.model.DisplayType;
import engine.graphics.model.voxel.Model;
import engine.graphics.texture.TextureAtlasRegion;
import engine.graphics.vertex.VertexDataBuffer;
import engine.util.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static engine.graphics.model.voxel.ModelLoadUtils.fillTransformationArray;

public class ItemGenerateModel implements Model {

    private static final float positivePixel = 1f / 32;
    private static final float negativePixel = -1f / 32;

    transient AssetURL url;
    AssetURL texture;
    Transform[] transforms;

    @Override
    public BakedModel bake(Function<AssetURL, TextureAtlasRegion> textureGetter) {
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
        fillTransformationArray(transforms);
        return new BakedModel(vertexes, transforms);
    }

    private int getAlpha(BufferedImage buffer, int pixelX, int pixelY) {
        if (pixelX < 0 || pixelX >= buffer.getWidth()) return 0;
        if (pixelY < 0 || pixelY >= buffer.getHeight()) return 0;
        return buffer.getPixel(pixelX, pixelY) & 0xff;
    }

    private void bakeNorth(List<float[]> vertexes, TextureAtlasRegion texture) {
        bakeQuad(vertexes, new float[]{
                        .5f, -.5f, negativePixel,
                        -.5f, -.5f, negativePixel,
                        -.5f, .5f, negativePixel,
                        .5f, .5f, negativePixel},
                texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV());
    }

    private void bakeSouth(List<float[]> vertexes, TextureAtlasRegion texture) {
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
        var normal = RenderingMath.calcNormalByVertices(positions);
        var normal1 = RenderingMath.calcNormalByVertices(positions);

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

    private static final class BakedModel implements engine.graphics.model.BakedModel {

        private final List<float[]> vertexes;
        private final Transform[] transforms;

        private BakedModel(List<float[]> vertexes, Transform[] transforms) {
            this.vertexes = vertexes;
            this.transforms = transforms;
        }

        @Override
        public void putVertexes(VertexDataBuffer buffer, int coveredFace) {
            for (var vertex : this.vertexes) {
                buffer.pos(vertex, 0).rgba(0xFFFFFFFF).tex(vertex, 3).normal(vertex, 5).endVertex();
            }
        }

        @Override
        public boolean isFullFace(Direction direction) {
            return false;
        }

        @Override
        public Transform getTransformation(DisplayType type) {
            return transforms[type.ordinal()];
        }
    }
}
