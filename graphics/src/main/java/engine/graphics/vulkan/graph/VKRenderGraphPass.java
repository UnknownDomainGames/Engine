package engine.graphics.vulkan.graph;

import engine.graphics.gl.graph.GLRenderTask;
import engine.graphics.graph.RenderPass;
import engine.graphics.graph.RenderPassInfo;
import engine.graphics.graph.RenderTask;
import engine.graphics.texture.FrameBuffer;

public class VKRenderGraphPass implements RenderPass {
    private final RenderPassInfo info;
    private final VKRenderTask task;

    public VKRenderGraphPass(RenderPassInfo renderPassInfo, VKRenderTask vkRenderTask) {
        this.info = renderPassInfo;
        this.task = vkRenderTask;
    }

    @Override
    public RenderPassInfo getInfo() {
        return info;
    }

    @Override
    public RenderTask getRenderTask() {
        return task;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
        return null;
    }
}
