package engine.graphics.vulkan.graph;

import engine.graphics.graph.*;
import engine.graphics.texture.Texture2D;
import engine.graphics.vulkan.CommandBuffer;

import java.util.*;
import java.util.stream.Collectors;

public class VKRenderTask implements RenderTask {

    private final RenderTaskInfo info;
    private final VKRenderGraph renderGraph;

    private final Map<String, VKRenderTaskAttachment> attachments;
    private final List<RenderTaskSetup> setups;
    private final Map<String, VKRenderGraphPass> passes;
    private final List<VKRenderGraphPass> sortedPasses;
    private final VKRenderGraphPass finalPass;

    public VKRenderTask(RenderTaskInfo info, VKRenderGraph renderGraph) {
        this.info = info;
        this.renderGraph = renderGraph;
        this.attachments = info.getRenderBuffers().stream().collect(Collectors.toUnmodifiableMap(RenderBufferInfo::getName, bufferInfo -> new VKRenderTaskAttachment(bufferInfo, this)));
        this.setups = List.copyOf(info.getSetups());
        this.passes = info.getPasses().stream().collect(Collectors.toUnmodifiableMap(RenderPassInfo::getName, passinfo -> new VKRenderGraphPass(passinfo, this)));
        this.finalPass = passes.get(info.getFinalPass());
        this.sortedPasses = sortPasses(passes.values());
    }

    private List<VKRenderGraphPass> sortPasses(Collection<VKRenderGraphPass> passes) {
        List<VKRenderGraphPass> sortedPasses = new ArrayList<>(passes.size());
        List<VKRenderGraphPass> needSortPasses = new LinkedList<>(passes);
        while (!needSortPasses.isEmpty()) {
            var iterator = needSortPasses.listIterator();
            while (iterator.hasNext()) {
                var pass = iterator.next();
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
    public RenderGraph getRenderGraph() {
        return renderGraph;
    }

    @Override
    public RenderPass getFinalPass() {
        return finalPass;
    }

    @Override
    public Texture2D getRenderBuffer(String name) {
        return null;
    }

    public void draw(Frame frame, CommandBuffer cmdBuffer) {
        int width = frame.getOutputWidth();
        int height = frame.getOutputHeight();
        if (frame.isResized()) {
//            attachments.values().forEach(renderBuffer -> renderBuffer.resize(width, height));
        }
        for (RenderTaskSetup setup : setups) {
            setup.setup(this, frame);
        }
        for (VKRenderGraphPass pass : sortedPasses) {
            pass.draw(frame, cmdBuffer);
        }
    }

    public void dispose() {
        passes.values().forEach(VKRenderGraphPass::dispose);
//        attachments.values().forEach(VKRenderTaskAttachment::dispose);
    }
}
