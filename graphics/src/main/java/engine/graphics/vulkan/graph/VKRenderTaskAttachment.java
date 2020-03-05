package engine.graphics.vulkan.graph;

import engine.graphics.gl.graph.GLRenderTask;
import engine.graphics.graph.RenderBufferInfo;

public class VKRenderTaskAttachment {
    private final RenderBufferInfo info;
    private final VKRenderTask renderTask;

    public VKRenderTaskAttachment(RenderBufferInfo renderBufferInfo, VKRenderTask vkRenderTask) {
        this.info = renderBufferInfo;
        this.renderTask = vkRenderTask;
    }

    public RenderBufferInfo getInfo() {
        return info;
    }

    public VKRenderTask getRenderTask() {
        return renderTask;
    }
}
