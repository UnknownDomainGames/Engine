package engine.graphics.graph;

import java.util.List;

public class RenderPassInfo {
    private String name;
    private List<String> dependencies = List.of();
    private List<DrawerInfo> drawers = List.of();
    private List<ColorOutputInfo> colorOutputs = List.of();
    private DepthOutputInfo depthOutput;

    public static RenderPassInfo renderPass() {
        return new RenderPassInfo();
    }

    public RenderPassInfo name(String name) {
        this.name = name;
        return this;
    }

    public RenderPassInfo dependsOn(String dependency) {
        this.dependencies = List.of(dependency);
        return this;
    }

    public RenderPassInfo dependsOn(String... dependencies) {
        this.dependencies = List.of(dependencies);
        return this;
    }

    public RenderPassInfo drawers(DrawerInfo drawer) {
        this.drawers = List.of(drawer);
        return this;
    }

    public RenderPassInfo drawers(DrawerInfo... drawers) {
        this.drawers = List.of(drawers);
        return this;
    }

    public RenderPassInfo outputs(ColorOutputInfo output) {
        this.colorOutputs = List.of(output);
        return this;
    }

    public RenderPassInfo outputs(ColorOutputInfo... outputs) {
        this.colorOutputs = List.of(outputs);
        return this;
    }

    public RenderPassInfo depth(DepthOutputInfo output) {
        this.depthOutput = output;
        return this;
    }

    public String getName() {
        return name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<DrawerInfo> getDrawers() {
        return drawers;
    }

    public List<ColorOutputInfo> getColorOutputs() {
        return colorOutputs;
    }

    public DepthOutputInfo getDepthOutput() {
        return depthOutput;
    }
}
