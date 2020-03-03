package engine.graphics.graph;

import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import org.joml.Vector4ic;

public interface Renderer {

    void setScissor(int x, int y, int width, int height);

    void setScissor(Vector4ic scissor);

    void drawStreamed(DrawMode drawMode, VertexDataBuf buf);

    void drawStreamed(DrawMode drawMode, VertexDataBuf buf, int first, int count);
}
