package engine.graphics.display;

import engine.graphics.glfw.GLFWContext;

import java.util.Collection;

public abstract class BaseWindowHelper implements WindowHelper {
    @Override
    public Collection<Screen> getScreens() {
        return GLFWContext.getScreens();
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
    public Screen getScreen(double x, double y) {
        return GLFWContext.getScreen(x, y);
    }
}
