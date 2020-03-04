package engine.graphics.mesh;

import engine.graphics.util.DrawMode;

public interface Mesh {

    DrawMode getDrawMode();

    int getVertexCount();

    void dispose();

    boolean isDisposed();
}
