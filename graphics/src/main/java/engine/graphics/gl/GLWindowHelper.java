package engine.graphics.gl;

import engine.graphics.display.BaseWindowHelper;
import engine.graphics.display.Window;
import engine.graphics.glfw.GLFWWindow;

public final class GLWindowHelper extends BaseWindowHelper {

    @Override
    public Window createWindow() {
        return new GLFWWindow();
    }

    @Override
    public Window createWindow(Window parent) {
        return new GLFWWindow(parent);
    }
}
