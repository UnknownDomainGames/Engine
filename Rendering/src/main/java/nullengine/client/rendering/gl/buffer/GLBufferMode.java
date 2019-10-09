package nullengine.client.rendering.gl.buffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

public class GLBufferMode {

    public static final GLBufferMode POINTS = new GLBufferMode(GL11.GL_POINTS);
    public static final GLBufferMode LINES = new GLBufferMode(GL11.GL_LINES);
    public static final GLBufferMode CONTINUOUS_LINES = new GLBufferMode(GL11.GL_LINE_STRIP);
    public static final GLBufferMode LINES_CLOSED = new GLBufferMode(GL11.GL_LINE_LOOP);
    public static final GLBufferMode TRIANGLES = new GLBufferMode(GL11.GL_TRIANGLES);
    public static final GLBufferMode CONTINUOUS_TRIANGLES = new GLBufferMode(GL11.GL_TRIANGLE_STRIP);
    public static final GLBufferMode TRIANGLE_FANS = new GLBufferMode(GL11.GL_TRIANGLE_FAN);
    @Deprecated
    public static final GLBufferMode QUADS = new GLBufferMode(GL11.GL_QUADS);
    @Deprecated
    public static final GLBufferMode CONTINUOUS_QUADS = new GLBufferMode(GL11.GL_QUAD_STRIP);

    public static final GLBufferMode ADJACENCY_LINES = new GLBufferMode(GL32.GL_LINES_ADJACENCY);
    public static final GLBufferMode ADJACENCY_CONTINUOUS_LINES = new GLBufferMode(GL32.GL_LINE_STRIP_ADJACENCY);
    public static final GLBufferMode ADJACENCY_TRIANGLES = new GLBufferMode(GL32.GL_TRIANGLES_ADJACENCY);
    public static final GLBufferMode ADJACENCY_CONTINUOUS_TRIANGLES = new GLBufferMode(GL32.GL_TRIANGLE_STRIP_ADJACENCY);

    private final int glenum;

    private GLBufferMode(int glenum) {
        this.glenum = glenum;
    }

    public int getOpenGlMode() {
        return glenum;
    }

}
