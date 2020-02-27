package engine.graphics.gl.graph;

import engine.graphics.graph.*;
import engine.util.SortedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GLRenderTask implements RenderTask {

    private final RenderTaskInfo info;
    private final GLRenderGraph renderGraph;

    private final Map<String, GLRenderTaskRB> renderBuffers = new HashMap<>();
    private final Map<String, GLRenderPass> passes = new HashMap<>();
    private final List<GLRenderPass> sortedPasses;
    private final GLRenderPass finalPass;

    public GLRenderTask(RenderTaskInfo info, GLRenderGraph renderGraph) {
        this.info = info;
        this.renderGraph = renderGraph;
        info.getRenderBuffers().forEach(renderBufferInfo ->
                renderBuffers.put(renderBufferInfo.getName(), new GLRenderTaskRB(renderBufferInfo, this)));
        info.getPasses().forEach(renderPassInfo ->
                passes.put(renderPassInfo.getName(), new GLRenderPass(renderPassInfo, this)));
        this.sortedPasses = SortedList.copyOf(passes.values(), (o1, o2) -> {
            if (o1.getInfo().getDependencies().contains(o2.getInfo().getName())) return 1;
            if (o2.getInfo().getDependencies().contains(o1.getInfo().getName())) return -1;
            return 0;
        });
        this.finalPass = passes.get(info.getFinalPass());
    }

    @Override
    public RenderTaskInfo getInfo() {
        return info;
    }

    @Override
    public RenderGraph getRenderGraph() {
        return renderGraph;
    }

    @Override
    public RenderPass getFinalPass() {
        return finalPass;
    }

    public Map<String, GLRenderTaskRB> getRenderBuffers() {
        return renderBuffers;
    }

    public GLRenderTaskRB getRenderBuffer(String name) {
        return renderBuffers.get(name);
    }

    public void draw(Frame frame) {
        int width = frame.getWidth();
        int height = frame.getHeight();
        if (frame.isResized()) {
            renderBuffers.values().forEach(renderBuffer -> renderBuffer.resizeWithViewport(width, height));
        }
        sortedPasses.forEach(pass -> pass.draw(frame));
        if (frame.isResized()) {
            renderBuffers.values().forEach(GLRenderTaskRB::resetResized);
        }
    }

    public void dispose() {
        passes.values().forEach(GLRenderPass::dispose);
        renderBuffers.values().forEach(GLRenderTaskRB::dispose);
    }
}
