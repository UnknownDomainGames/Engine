package engine.graphics.graph;

public interface RenderTask {
    RenderTaskInfo getInfo();

    RenderGraph getRenderGraph();

    RenderPass getFinalPass();
}
