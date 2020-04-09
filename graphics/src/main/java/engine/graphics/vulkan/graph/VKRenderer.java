package engine.graphics.vulkan.graph;

import engine.graphics.graph.Renderer;
import engine.graphics.mesh.Mesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vulkan.CommandBuffer;
import org.joml.Vector4i;
import org.joml.Vector4ic;

public class VKRenderer implements Renderer {

    private CommandBuffer commandBuffer;

    public VKRenderer(CommandBuffer commandBuffer) {
        this.commandBuffer = commandBuffer;
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        setScissor(new Vector4i(x, y, width, height));
    }

    @Override
    public void setScissor(Vector4ic scissor) {
        commandBuffer.setScissor(scissor);
    }

    @Override
    public void drawMesh(Mesh mesh) {
        drawMesh(mesh, 0, mesh.getVertexCount());
    }

    @Override
    public void drawMesh(Mesh mesh, int first, int count) {

    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuf buf) {
        drawStreamed(drawMode, buf, 0, buf.getVertexCount());
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuf buf, int first, int count) {

    }
}
