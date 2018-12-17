package unknowndomain.engine.client.rendering.texture;

public class GLTexturePart {

    private final GLTexture texture;
    private final int offsetX, offsetY, width, height;

    public GLTexturePart(GLTexture texture) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    public GLTexturePart(GLTexture texture, int offsetX, int offsetY, int width, int height) {
        this.texture = texture;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    public GLTexture getTexture() {
        return texture;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
