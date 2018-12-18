package unknowndomain.engine.client.rendering.texture;

public class GLTexturePart implements Texture {

    public static GLTexturePart of(GLTexture texture) {
        return new GLTexturePart(texture, 0,0,1,1);
    }

    public static GLTexturePart of(GLTexture texture, int offsetX, int offsetY, int width, int height) {
        return new GLTexturePart(texture, (float) offsetX / texture.getWidth(),
                (float) offsetY / texture.getHeight(),
                (float) (offsetX + width) / texture.getWidth(),
                (float) (offsetY + height) / texture.getHeight());
    }

    private final GLTexture texture;
    private final float minU, minV, maxU, maxV;

    public GLTexturePart(GLTexture texture, float minU, float minV, float maxU, float maxV) {
        this.texture = texture;
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    public GLTexture getTexture() {
        return texture;
    }

    public float getMinU() {
        return minU;
    }

    public float getMinV() {
        return minV;
    }

    public float getMaxU() {
        return maxU;
    }

    public float getMaxV() {
        return maxV;
    }
}
