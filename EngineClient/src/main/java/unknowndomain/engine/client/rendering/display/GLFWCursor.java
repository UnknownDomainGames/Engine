package unknowndomain.engine.client.rendering.display;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWCursor implements Cursor {

    private static final int[] GLFW_CURSOR_STATES = new int[]{GLFW_CURSOR_NORMAL, GLFW_CURSOR_HIDDEN, GLFW_CURSOR_DISABLED};
    private static final int[] GLFW_CURSOR_SHAPES = new int[]{GLFW_ARROW_CURSOR, GLFW_IBEAM_CURSOR, GLFW_CROSSHAIR_CURSOR, GLFW_HAND_CURSOR, GLFW_HRESIZE_CURSOR, GLFW_VRESIZE_CURSOR};

    private final long windowId;

    private CursorState state = CursorState.NORMAL;
    private CursorShape shape = CursorShape.ARROW;

    public GLFWCursor(long windowId) {
        this.windowId = windowId;
    }

    @Nonnull
    @Override
    public CursorState getCursorState() {
        return state;
    }

    @Override
    public void setCursorState(@Nonnull CursorState state) {
        this.state = Objects.requireNonNull(state);
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_STATES[state.ordinal()]);
    }

    @Nonnull
    @Override
    public CursorShape getCursorShape() {
        return shape;
    }

    @Override
    public void setCursorShape(@Nonnull CursorShape shape) {
        this.shape = Objects.requireNonNull(shape);
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_SHAPES[shape.ordinal()]);
    }
}
