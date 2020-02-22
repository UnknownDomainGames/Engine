package engine.graphics.graph;

import java.util.List;

public class RenderTaskInfo {
    private String name;
    private List<RenderBufferInfo> renderBuffers;
    private List<RenderPassInfo> passes;

    public static RenderTaskInfo renderTask() {
        return new RenderTaskInfo();
    }

    public RenderTaskInfo name(String name) {
        this.name = name;
        return this;
    }

    public RenderTaskInfo renderBuffers(RenderBufferInfo... renderBuffers) {
        this.renderBuffers = List.of(renderBuffers);
        return this;
    }

    public RenderTaskInfo passes(RenderPassInfo... passes) {
        this.passes = List.of(passes);
        return this;
    }

    public String getName() {
        return name;
    }

    public List<RenderBufferInfo> getRenderBuffers() {
        return renderBuffers;
    }

    public List<RenderPassInfo> getPasses() {
        return passes;
    }
}
