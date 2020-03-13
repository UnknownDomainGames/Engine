package engine.graphics.vulkan.graph;

import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderPass;
import engine.graphics.graph.RenderTask;
import engine.graphics.graph.RenderTaskInfo;
import engine.graphics.texture.Texture2D;
import engine.util.SortedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VKRenderTask implements RenderTask {

    private final RenderTaskInfo info;
    private final VKRenderGraph renderGraph;

    private final Map<String, VKRenderTaskAttachment> attachments = new HashMap<>();
    private final Map<String, VKRenderGraphPass> passes = new HashMap<>();
    private final List<VKRenderGraphPass> sortedPasses;
    private final VKRenderGraphPass finalPass;

    public VKRenderTask(RenderTaskInfo info, VKRenderGraph renderGraph) {
        this.info = info;
        this.renderGraph = renderGraph;
        info.getRenderBuffers().forEach(renderBufferInfo ->
                attachments.put(renderBufferInfo.getName(), new VKRenderTaskAttachment(renderBufferInfo, this)));
        info.getPasses().forEach(renderPassInfo ->
                passes.put(renderPassInfo.getName(), new VKRenderGraphPass(renderPassInfo, this)));
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

    @Override
    public Texture2D getRenderBuffer(String name) {
        return null;
    }
}
