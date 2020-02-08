package engine.graphics.gl;

import engine.graphics.display.Monitor;
import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.glfw.GLFWContext;
import engine.graphics.glfw.GLFWWindow;

import java.util.Collection;

public final class GLWindowHelper implements WindowHelper {
    @Override
    public Collection<Monitor> getMonitors() {
        return GLFWContext.getMonitors();
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return GLFWContext.getPrimaryMonitor();
    }

    @Override
    public Window createWindow() {
        return new GLFWWindow();
    }
}
