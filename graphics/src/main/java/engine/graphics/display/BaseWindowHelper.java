package engine.graphics.display;

import engine.graphics.display.Screen;
import engine.graphics.display.WindowHelper;
import engine.graphics.glfw.GLFWContext;

import java.util.Collection;

public abstract class BaseWindowHelper implements WindowHelper {
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
}
