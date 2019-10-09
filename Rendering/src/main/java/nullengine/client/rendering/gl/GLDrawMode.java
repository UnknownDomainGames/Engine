package nullengine.client.rendering.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

public class GLDrawMode {

    public static final GLDrawMode POINTS = new GLDrawMode(GL11.GL_POINTS);
    public static final GLDrawMode LINES = new GLDrawMode(GL11.GL_LINES);
    public static final GLDrawMode CONTINUOUS_LINES = new GLDrawMode(GL11.GL_LINE_STRIP);
    public static final GLDrawMode LINES_CLOSED = new GLDrawMode(GL11.GL_LINE_LOOP);
    public static final GLDrawMode TRIANGLES = new GLDrawMode(GL11.GL_TRIANGLES);
    public static final GLDrawMode CONTINUOUS_TRIANGLES = new GLDrawMode(GL11.GL_TRIANGLE_STRIP);
    public static final GLDrawMode TRIANGLE_FANS = new GLDrawMode(GL11.GL_TRIANGLE_FAN);
    @Deprecated
    public static final GLDrawMode QUADS = new GLDrawMode(GL11.GL_QUADS);
    @Deprecated
    public static final GLDrawMode CONTINUOUS_QUADS = new GLDrawMode(GL11.GL_QUAD_STRIP);

    public static final GLDrawMode ADJACENCY_LINES = new GLDrawMode(GL32.GL_LINES_ADJACENCY);
    public static final GLDrawMode ADJACENCY_CONTINUOUS_LINES = new GLDrawMode(GL32.GL_LINE_STRIP_ADJACENCY);
    public static final GLDrawMode ADJACENCY_TRIANGLES = new GLDrawMode(GL32.GL_TRIANGLES_ADJACENCY);
    public static final GLDrawMode ADJACENCY_CONTINUOUS_TRIANGLES = new GLDrawMode(GL32.GL_TRIANGLE_STRIP_ADJACENCY);

    public final int gl;

    private GLDrawMode(int gl) {
        this.gl = gl;
    }
}
