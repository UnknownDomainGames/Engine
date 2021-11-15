package engine.graphics.graph;

import engine.graphics.mesh.Mesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;
import org.joml.Vector4ic;

public interface Renderer {

    void setScissor(int x, int y, int width, int height);

    void setScissor(Vector4ic scissor);

    void drawMesh(Mesh mesh);

    void drawMesh(Mesh mesh, int first, int count);

    void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer);

    void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer, int first, int count);
}
