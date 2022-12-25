package engine.graphics.gl.graph;

import engine.graphics.graph.*;
import engine.graphics.texture.Texture;

import java.util.*;
import java.util.stream.Collectors;

public final class GLRenderTask implements RenderTask {

    private final RenderTaskInfo info;
    private final GLRenderGraph renderGraph;

    private final Map<String, GLRenderBufferProxy> renderBuffers;
    private final List<RenderTaskSetup> setups;
    private final Map<String, GLRenderPass> passes;
    private final GLRenderPass finalPass;
    private final List<GLRenderPass> sortedPasses;

    public GLRenderTask(RenderTaskInfo info, GLRenderGraph renderGraph) {
        this.info = info;
        this.renderGraph = renderGraph;
        this.renderBuffers = info.getRenderBuffers().stream().collect(Collectors.toUnmodifiableMap(
                RenderBufferInfo::getName, renderBufferInfo -> new GLRenderBufferProxy(renderBufferInfo, this)));
        this.setups = List.copyOf(info.getSetups());
        this.passes = info.getPasses().stream().collect(Collectors.toUnmodifiableMap(
                RenderPassInfo::getName, renderPassInfo -> new GLRenderPass(renderPassInfo, this)));
        this.finalPass = passes.get(info.getFinalPass());
        this.sortedPasses = sortPasses(passes.values());
    }

    private List<GLRenderPass> sortPasses(Collection<GLRenderPass> passes) {
        List<GLRenderPass> sortedPasses = new ArrayList<>(passes.size());
        List<GLRenderPass> needSortPasses = new LinkedList<>(passes);
        while (!needSortPasses.isEmpty()) {
            var iterator = needSortPasses.listIterator();
            while (iterator.hasNext()) {
                GLRenderPass pass = iterator.next();
                if (needSortPasses.stream().noneMatch($ -> pass.getDependencies().contains($.getName()))) {
                    sortedPasses.add(pass);
                    iterator.remove();
                }
            }
        }
        return List.copyOf(sortedPasses);
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
            for (GLRenderBufferProxy renderBuffer : renderBuffers.values()) {
                renderBuffer.resize(width, height);
            }
        }
        FrameContext frameContext = new FrameContext(frame, args);
        for (RenderTaskSetup setup : setups) {
            setup.setup(this, frameContext);
        }
        for (GLRenderPass pass : sortedPasses) {
            pass.draw(frameContext);
        }
    }

    public void dispose() {
        passes.values().forEach(GLRenderPass::dispose);
        renderBuffers.values().forEach(GLRenderBufferProxy::dispose);
    }
}
