package engine.graphics.graph;

import java.util.List;

public class RenderTaskInfo {
    private String name;
    private List<RenderBufferInfo> renderBuffers;
    private List<RenderPassInfo> passes;

    public static RenderTaskInfo renderTask() {
        return new RenderTaskInfo();
    }

    public String getName() {
        return name;
    }

    public RenderTaskInfo setName(String name) {
        this.name = name;
        return this;
    }

    public List<RenderBufferInfo> getRenderBuffers() {
        return renderBuffers;
    }

    public RenderTaskInfo setRenderBuffers(RenderBufferInfo... renderBuffers) {
        this.renderBuffers = List.of(renderBuffers);
        return this;
    }

    public List<RenderPassInfo> getPasses() {
        return passes;
    }

    public RenderTaskInfo setPasses(RenderPassInfo... passes) {
        this.passes = List.of(passes);
        return this;
    }
}
