package nullengine.client.rendering.gl;

import nullengine.client.rendering.display.Monitor;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.WindowHelper;
import nullengine.client.rendering.glfw.GLFWContext;
import nullengine.client.rendering.glfw.GLFWWindow;

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
