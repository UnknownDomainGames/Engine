package engine.graphics.graph;

import engine.graphics.util.CullMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderPassInfo {
    private String name;
    private final List<String> dependencies = new ArrayList<>();
    private CullMode cullMode = CullMode.DISABLED;
    private final List<DrawerInfo> drawers = new ArrayList<>();
    private final List<ColorOutputInfo> colorOutputs = new ArrayList<>();
    private DepthOutputInfo depthOutput;

    public static RenderPassInfo renderPass() {
        return new RenderPassInfo();
    }

    public String getName() {
        return name;
    }

    public RenderPassInfo setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public RenderPassInfo dependsOn(String dependency) {
        this.dependencies.add(dependency);
        return this;
    }

    public RenderPassInfo dependsOn(String... dependencies) {
        Collections.addAll(this.dependencies, dependencies);
        return this;
    }

    public CullMode getCullMode() {
        return cullMode;
    }

    public RenderPassInfo setCullMode(CullMode cullMode) {
        this.cullMode = cullMode;
        return this;
    }

    public List<DrawerInfo> getDrawers() {
        return drawers;
    }

    public RenderPassInfo addDrawers(DrawerInfo drawer) {
        this.drawers.add(drawer);
        return this;
    }

    public RenderPassInfo addDrawers(DrawerInfo... drawers) {
        Collections.addAll(this.drawers, drawers);
        return this;
    }

    public List<ColorOutputInfo> getColorOutputs() {
        return colorOutputs;
    }

    public RenderPassInfo addColorOutputs(ColorOutputInfo output) {
        this.colorOutputs.add(output);
        return this;
    }

    public RenderPassInfo addColorOutputs(ColorOutputInfo... outputs) {
        Collections.addAll(this.colorOutputs, outputs);
        return this;
    }

    public DepthOutputInfo getDepthOutput() {
        return depthOutput;
    }

    public RenderPassInfo setDepthOutput(DepthOutputInfo output) {
        this.depthOutput = output;
        return this;
    }
}
