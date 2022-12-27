package engine.graphics.graph;

import engine.graphics.mesh.Mesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;

public interface Renderer {

    void setScissor(int x, int y, int width, int height);

    void clearScissor();

    void drawMesh(Mesh mesh);

    void drawMesh(Mesh mesh, int first, int count);

    void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer);

    void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer, int first, int count);
}
