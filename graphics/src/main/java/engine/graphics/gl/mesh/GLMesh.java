package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.mesh.Mesh;
import engine.graphics.util.Cleaner;
import engine.graphics.util.DrawMode;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL45C;

public abstract class GLMesh implements Mesh {
    protected int id;
    protected Cleaner.Cleanable cleanable;
    protected GLDrawMode drawMode;
    protected int vertexCount;

    public GLMesh() {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            id = GL45C.glCreateVertexArrays();
        } else {
            id = GL30C.glGenVertexArrays();
        }
        cleanable = GLCleaner.registerVertexArray(this, id);
    }

    public abstract void draw(int start, int count);

    public int getId() {
        return id;
    }

    @Override
    public DrawMode getDrawMode() {
        return drawMode.peer;
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    public void bind() {
        GL30C.glBindVertexArray(id);
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }
}
