package engine.graphics.gl.graph;

import engine.graphics.graph.*;
import engine.graphics.texture.Texture;
import engine.util.SortedList;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class GLRenderTask implements RenderTask {

    private final RenderTaskInfo info;
    private final GLRenderGraph renderGraph;

    private final Map<String, GLRenderBufferProxy> renderBuffers;
    private final Map<String, GLRenderPass> passes;
    private final List<GLRenderPass> sortedPasses;
    private final GLRenderPass finalPass;
    private final List<BiConsumer<Frame, RenderTask>> setups;


    public GLRenderTask(RenderTaskInfo info, GLRenderGraph renderGraph) {
        this.info = info;
        this.renderGraph = renderGraph;
        this.renderBuffers = info.getRenderBuffers().stream().collect(Collectors.toUnmodifiableMap(
                RenderBufferInfo::getName, renderBufferInfo -> new GLRenderBufferProxy(renderBufferInfo, this)));
        this.passes = info.getPasses().stream().collect(Collectors.toUnmodifiableMap(
                RenderPassInfo::getName, renderPassInfo -> new GLRenderPass(renderPassInfo, this)));
        this.sortedPasses = SortedList.copyOf(passes.values(), (o1, o2) -> {
            if (o1.getInfo().getDependencies().contains(o2.getInfo().getName())) return 1;
            if (o2.getInfo().getDependencies().contains(o1.getInfo().getName())) return -1;
            return 0;
        });
        this.setups = List.copyOf(info.getSetups());
        this.finalPass = passes.get(info.getFinalPass());
    }

    @Override
    public RenderTaskInfo getInfo() {
        return info;
    }

    @Override
    public GLRenderGraph getRenderGraph() {
        return renderGraph;
    }

    @Override
    public RenderPass getFinalPass() {
        return finalPass;
    }

    @Override
    public Texture getRenderBuffer(String name) {
        return getRenderBufferProxy(name).getTexture();
    }

    public GLRenderBufferProxy getRenderBufferProxy(String name) {
        return renderBuffers.get(name);
    }

    public void draw(Frame frame, Map<String, Object> args) {
        int width = frame.getOutputWidth();
        int height = frame.getOutputHeight();
        if (frame.isResized()) {
            renderBuffers.values().forEach(renderBuffer -> renderBuffer.resize(width, height));
        }
        setups.forEach(consumer -> consumer.accept(frame, this));
        sortedPasses.forEach(pass -> pass.draw(frame));
    }

    public void dispose() {
        passes.values().forEach(GLRenderPass::dispose);
        renderBuffers.values().forEach(GLRenderBufferProxy::dispose);
    }
}
