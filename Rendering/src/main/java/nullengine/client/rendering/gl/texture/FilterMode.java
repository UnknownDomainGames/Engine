package nullengine.client.rendering.gl.texture;

import static org.lwjgl.opengl.GL11.*;

public enum FilterMode {
    LINEAR(GL_LINEAR, false),
    NEAREST(GL_NEAREST, false),
    NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST, true),
    LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST, true),
    NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR, true),
    LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR, true);

    public final int gl;
    public final boolean mipmap;

    FilterMode(int gl, boolean mipmap) {
        this.gl = gl;
        this.mipmap = mipmap;
    }
}
