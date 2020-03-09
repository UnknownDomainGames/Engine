package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.mesh.Mesh;
import engine.graphics.util.Cleaner;
import engine.graphics.util.DrawMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

public abstract class GLMesh implements Mesh {
    protected int id;
    protected Cleaner.Disposable disposable;
    protected GLDrawMode drawMode;
    protected int vertexCount;

    public GLMesh() {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            id = GL45.glCreateVertexArrays();
        } else {
            id = GL30.glGenVertexArrays();
        }
        disposable = GLCleaner.registerVertexArray(this, id);
    }

    protected abstract boolean hasIndices();

    protected abstract int getIndicesType();

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
        GL30.glBindVertexArray(id);
    }

    public void draw(int first, int count) {
        bind();
        if (hasIndices()) GL11.glDrawElements(drawMode.gl, vertexCount, getIndicesType(), 0);
        else GL11.glDrawArrays(drawMode.gl, first, count);

    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }
}
