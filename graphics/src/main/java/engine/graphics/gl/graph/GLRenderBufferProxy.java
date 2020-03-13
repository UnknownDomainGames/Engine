package engine.graphics.gl.graph;

import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.graph.RenderBufferInfo;

public final class GLRenderBufferProxy {

    private final RenderBufferInfo info;
    private final GLRenderTask renderTask;

    private GLTexture2D texture;

    public GLRenderBufferProxy(RenderBufferInfo info, GLRenderTask renderTask) {
        this.info = info;
        this.renderTask = renderTask;
    }

    public RenderBufferInfo getInfo() {
        return info;
    }

    public GLRenderTask getRenderTask() {
        return renderTask;
    }

    public GLTexture2D getTexture() {
        return texture;
    }

    public void resize(int width, int height) {
        if (texture != null) texture.dispose();
        texture = GLTexture2D.builder()
                .format(info.getFormat())
                .build(info.getSize().compute(width, height));
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
