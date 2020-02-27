package engine.graphics.gl.graph;

import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.graph.RenderBufferInfo;
import engine.graphics.graph.RenderBufferSize;

public final class GLRenderTaskRB {

    private final RenderBufferInfo info;
    private final GLRenderTask renderTask;

    private GLTexture2D texture;

    private boolean resized;

    public GLRenderTaskRB(RenderBufferInfo info, GLRenderTask renderTask) {
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

    public boolean isResized() {
        return resized;
    }

    public void resizeWithViewport(int width, int height) {
        resized = true;
        if (texture != null) {
            texture.dispose();
        }
        RenderBufferSize size = info.getSize();
        size.resizeWithViewport(width, height);
        texture = GLTexture2D.builder().format(info.getFormat()).build(size.getWidth(), size.getHeight());
    }

    public void resetResized() {
        resized = false;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
