package engine.graphics.vulkan.graph;

import engine.graphics.graph.Renderer;
import engine.graphics.mesh.Mesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vulkan.CommandBuffer;

public class VKRenderer implements Renderer {

    private CommandBuffer commandBuffer;

    public VKRenderer(CommandBuffer commandBuffer) {
        this.commandBuffer = commandBuffer;
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        commandBuffer.setScissor(x, y, width, height);
    }

    @Override
    public void clearScissor() {
        // TODO: clear scissor.
    }

    @Override
    public void drawMesh(Mesh mesh) {
        drawMesh(mesh, 0, mesh.getVertexCount());
    }

    @Override
    public void drawMesh(Mesh mesh, int first, int count) {

    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer) {
        drawStreamed(drawMode, buffer, 0, buffer.getVertexCount());
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer, int first, int count) {

    }
}
