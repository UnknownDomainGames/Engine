package engine.graphics.graph;

import engine.graphics.texture.TextureFormat;

public class RenderBufferInfo {

    private String name;
    private TextureFormat format;
    private RenderBufferSize size;

    public static RenderBufferInfo renderBuffer() {
        return new RenderBufferInfo();
    }

    public RenderBufferInfo name(String name) {
        this.name = name;
        return this;
    }

    public RenderBufferInfo format(TextureFormat format) {
        this.format = format;
        return this;
    }

    public RenderBufferInfo fixedSize(int width, int height) {
        size = new RenderBufferSize.FixedSize(width, height);
        return this;
    }

    public RenderBufferInfo relativeSize(float scaleWidth, float scaleHeight) {
        size = new RenderBufferSize.ViewportRelativeSize(scaleWidth, scaleHeight);
        return this;
    }

    public String getName() {
        return name;
    }

    public TextureFormat getFormat() {
        return format;
    }

    public RenderBufferSize getSize() {
        return size;
    }


}
