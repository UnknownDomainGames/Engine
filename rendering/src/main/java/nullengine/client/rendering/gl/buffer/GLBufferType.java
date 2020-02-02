package nullengine.client.rendering.gl.buffer;

import static org.lwjgl.opengl.GL40.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL40.GL_ELEMENT_ARRAY_BUFFER;

// TODO: Fill all type of buffer.
public enum GLBufferType {
    ARRAY_BUFFER(GL_ARRAY_BUFFER),
    ELEMENT_ARRAY_BUFFER(GL_ELEMENT_ARRAY_BUFFER);

    public final int gl;

    GLBufferType(int gl) {
        this.gl = gl;
    }
}
