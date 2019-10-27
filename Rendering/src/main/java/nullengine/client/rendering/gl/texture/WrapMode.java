package nullengine.client.rendering.gl.texture;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public enum WrapMode {
    CLAMP(GL_CLAMP),
    REPEAT(GL_REPEAT),
    CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE);

    public final int gl;

    WrapMode(int gl) {
        this.gl = gl;
    }
}
