package engine.graphics.graph;

import engine.graphics.texture.TextureFormat;

public class RenderBufferInfo {

    private String name;
    private TextureFormat format;
    private RenderBufferSize size;

    public static RenderBufferInfo renderBuffer() {
        return new RenderBufferInfo();
    }

    public String getName() {
        return name;
    }

    public RenderBufferInfo setName(String name) {
        this.name = name;
        return this;
    }

    public TextureFormat getFormat() {
        return format;
    }

    public RenderBufferInfo setFormat(TextureFormat format) {
        this.format = format;
        return this;
    }

    public RenderBufferInfo setFixedSize(int width, int height) {
        size = new RenderBufferSize.FixedSize(width, height);
        return this;
    }

    public RenderBufferInfo setRelativeSize(float scaleWidth, float scaleHeight) {
        size = new RenderBufferSize.ViewportRelativeSize(scaleWidth, scaleHeight);
        return this;
    }

    public RenderBufferSize getSize() {
        return size;
    }


}
