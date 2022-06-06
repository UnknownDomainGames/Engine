package engine.graphics.display;

import engine.graphics.GraphicsEngine;

import java.util.Collection;
import java.util.List;

public interface Screen {
    static Screen getPrimary() {
        return GraphicsEngine.getGraphicsBackend().getWindowHelper().getPrimaryScreen();
    }

    static Collection<Screen> getScreens() {
        return GraphicsEngine.getGraphicsBackend().getWindowHelper().getScreens();
    }

    static Screen getScreen(String name) {
        return GraphicsEngine.getGraphicsBackend().getWindowHelper().getScreen(name);
    }

    static Screen getScreen(double x, double y) {
        return GraphicsEngine.getGraphicsBackend().getWindowHelper().getScreen(x, y);
    }

    long getPointer();

    String getName();

    int getWorkareaX();

    int getWorkareaY();

    int getWorkareaWidth();

    int getWorkareaHeight();

    int getPhysicsWidth();

    int getPhysicsHeight();

    float getScaleX();

    float getScaleY();

    VideoMode getVideoMode();

    List<VideoMode> getVideoModes();
}
