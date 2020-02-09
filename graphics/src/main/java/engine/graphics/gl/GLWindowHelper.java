package engine.graphics.gl;

import engine.graphics.display.Screen;
import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.glfw.GLFWContext;
import engine.graphics.glfw.GLFWWindow;

import java.util.Collection;

public final class GLWindowHelper implements WindowHelper {
    @Override
    public Collection<Screen> getScreens() {
        return GLFWContext.getNameToScreen();
    }

    @Override
    public Screen getPrimaryScreen() {
        return GLFWContext.getPrimaryScreen();
    }

    @Override
    public Screen getScreen(String name) {
        return GLFWContext.getScreen(name);
    }

    @Override
    public Window createWindow() {
        return new GLFWWindow();
    }
}
