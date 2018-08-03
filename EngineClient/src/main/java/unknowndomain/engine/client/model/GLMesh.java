package unknowndomain.engine.client.model;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class GLMesh {
    private int vao;
    private int[] vbos;
    private int counts;

    private int mode; // gl draw mode

    public GLMesh(int vao, int[] vbos, int counts, int mode) {
        this.vao = vao;
        this.vbos = vbos;
        this.counts = counts;
        this.mode = mode;
    }

    public void dispose() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vbos) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }

    public void render() {
        // Draw the mesh
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        // glEnableVertexAttribArray(2);

        glDrawElements(mode, counts, GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        // glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    @Override
    public String toString() {
        return "GLMesh { vao: " + vao + ", count: " + counts + " }" ;
    }
}
