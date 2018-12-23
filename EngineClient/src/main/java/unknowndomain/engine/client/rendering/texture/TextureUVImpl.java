package unknowndomain.engine.client.rendering.texture;

public class TextureUVImpl implements TextureUV {

    private float minU, minV, maxU, maxV;

    public TextureUVImpl() {
    }

    public void init(float minU, float minV, float maxU, float maxV) {
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    public void init(int textureMapWidth, int textureMapHeight, int offsetX, int offsetY, int width, int height) {
        init((float) offsetX / textureMapWidth,
                (float) offsetY / textureMapHeight,
                (float) (offsetX + width) / textureMapWidth,
                (float) (offsetY + height) / textureMapHeight);
    }

    @Override
    public float getMinU() {
        return minU;
    }

    @Override
    public float getMinV() {
        return minV;
    }

    @Override
    public float getMaxU() {
        return maxU;
    }

    @Override
    public float getMaxV() {
        return maxV;
    }
}
