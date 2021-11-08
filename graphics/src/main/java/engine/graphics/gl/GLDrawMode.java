package engine.graphics.gl;

import engine.graphics.util.DrawMode;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL40C;

public enum GLDrawMode {

    POINTS(DrawMode.POINTS, GL11C.GL_POINTS),
    LINES(DrawMode.LINES, GL11C.GL_LINES),
    LINE_STRIP(DrawMode.LINE_STRIP, GL11C.GL_LINE_STRIP),
    LINE_LOOP(DrawMode.LINE_LOOP, GL11C.GL_LINE_LOOP),
    TRIANGLES(DrawMode.TRIANGLES, GL11C.GL_TRIANGLES),
    TRIANGLE_STRIP(DrawMode.TRIANGLE_STRIP, GL11C.GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(DrawMode.TRIANGLE_FAN, GL11C.GL_TRIANGLE_FAN),
    LINES_ADJACENCY(DrawMode.LINES_ADJACENCY, GL32C.GL_LINES_ADJACENCY),
    LINE_STRIP_ADJACENCY(DrawMode.LINE_STRIP_ADJACENCY, GL32C.GL_LINE_STRIP_ADJACENCY),
    TRIANGLES_ADJACENCY(DrawMode.TRIANGLES_ADJACENCY, GL32C.GL_TRIANGLES_ADJACENCY),
    TRIANGLE_STRIP_ADJACENCY(DrawMode.TRIANGLE_STRIP_ADJACENCY, GL32C.GL_TRIANGLE_STRIP_ADJACENCY),
    PATCHES(DrawMode.PATCHES, GL40C.GL_PATCHES);

    private static final GLDrawMode[] VALUES = values();

    public final DrawMode peer;
    public final int gl;

    public static GLDrawMode valueOf(DrawMode drawMode) {
        return VALUES[drawMode.ordinal()];
    }

    GLDrawMode(DrawMode peer, int gl) {
        this.peer = peer;
        this.gl = gl;
    }
}
