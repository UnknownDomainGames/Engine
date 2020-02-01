package nullengine.client.rendering.gl;

import nullengine.client.rendering.util.DrawMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

public enum GLDrawMode {

    POINTS(DrawMode.POINTS, GL11.GL_POINTS),
    LINES(DrawMode.LINES, GL11.GL_LINES),
    LINE_STRIP(DrawMode.LINE_STRIP, GL11.GL_LINE_STRIP),
    LINE_LOOP(DrawMode.LINE_LOOP, GL11.GL_LINE_LOOP),
    TRIANGLES(DrawMode.TRIANGLES, GL11.GL_TRIANGLES),
    TRIANGLES_STRIP(DrawMode.TRIANGLES_STRIP, GL11.GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(DrawMode.TRIANGLE_FAN, GL11.GL_TRIANGLE_FAN),
    @Deprecated
    QUADS(DrawMode.QUADS, GL11.GL_QUADS),
    @Deprecated
    QUAD_STRIP(DrawMode.QUAD_STRIP, GL11.GL_QUAD_STRIP),
    LINES_ADJACENCY(DrawMode.LINES_ADJACENCY, GL32.GL_LINES_ADJACENCY),
    LINE_STRIP_ADJACENCY(DrawMode.LINE_STRIP_ADJACENCY, GL32.GL_LINE_STRIP_ADJACENCY),
    TRIANGLES_ADJACENCY(DrawMode.TRIANGLES_ADJACENCY, GL32.GL_TRIANGLES_ADJACENCY),
    TRIANGLE_STRIP_ADJACENCY(DrawMode.TRIANGLE_STRIP_ADJACENCY, GL32.GL_TRIANGLE_STRIP_ADJACENCY);

    public final DrawMode peer;
    public final int gl;

    public static GLDrawMode valueOf(DrawMode drawMode) {
        return values()[drawMode.ordinal()];
    }

    GLDrawMode(DrawMode peer, int gl) {
        this.peer = peer;
        this.gl = gl;
    }
}
