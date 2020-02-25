package engine.graphics.graph;

import engine.graphics.util.CullMode;

import java.util.List;

public class RenderPassInfo {
    private String name;
    private List<String> dependencies = List.of();
    private CullMode cullMode = CullMode.DISABLED;
    private List<DrawerInfo> drawers = List.of();
    private List<ColorOutputInfo> colorOutputs = List.of();
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
        this.dependencies = List.of(dependency);
        return this;
    }

    public RenderPassInfo dependsOn(String... dependencies) {
        this.dependencies = List.of(dependencies);
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

    public RenderPassInfo setDrawers(DrawerInfo drawer) {
        this.drawers = List.of(drawer);
        return this;
    }

    public RenderPassInfo setDrawers(DrawerInfo... drawers) {
        this.drawers = List.of(drawers);
        return this;
    }

    public List<ColorOutputInfo> getColorOutputs() {
        return colorOutputs;
    }

    public RenderPassInfo setColorOutputs(ColorOutputInfo output) {
        this.colorOutputs = List.of(output);
        return this;
    }

    public RenderPassInfo setColorOutputs(ColorOutputInfo... outputs) {
        this.colorOutputs = List.of(outputs);
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
