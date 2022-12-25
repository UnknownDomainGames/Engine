package engine.graphics.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderTaskInfo {
    private String name;
    private String finalPass;
    private final List<RenderBufferInfo> renderBuffers = new ArrayList<>();
    private final List<RenderTaskSetup> setups = new ArrayList<>();
    private final List<RenderPassInfo> passes = new ArrayList<>();

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

    public String getFinalPass() {
        return finalPass;
    }

    public RenderTaskInfo setFinalPass(String finalPass) {
        this.finalPass = finalPass;
        return this;
    }

    public List<RenderBufferInfo> getRenderBuffers() {
        return renderBuffers;
    }

    public RenderTaskInfo addRenderBuffers(RenderBufferInfo... renderBuffers) {
        Collections.addAll(this.renderBuffers, renderBuffers);
        return this;
    }

    public List<RenderTaskSetup> getSetups() {
        return setups;
    }

    public RenderTaskInfo addSetup(RenderTaskSetup setup) {
        this.setups.add(setup);
        return this;
    }

    public List<RenderPassInfo> getPasses() {
        return passes;
    }

    public RenderTaskInfo addPasses(RenderPassInfo... passes) {
        Collections.addAll(this.passes, passes);
        return this;
    }
}
