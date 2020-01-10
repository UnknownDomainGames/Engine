package nullengine.client.gui.internal.impl;

import nullengine.client.gui.Scene;
import nullengine.client.gui.internal.SceneHelper;
import nullengine.client.rendering.display.Window;

import java.util.HashMap;
import java.util.Map;

public final class SceneHelperImpl extends SceneHelper {

    final Map<Window, Scene> boundWindows = new HashMap<>();

    @Override
    public void bindWindow(Scene scene, Window window) {
        boundWindows.put(window, scene);
        getSceneAccessor().getMutableWindowProperty(scene).set(window);

        scene.setViewport(window.getWidth(), window.getHeight(), window.getContentScaleX(), window.getContentScaleY());
        scene.update();
    }

    @Override
    public void unbindWindow(Scene scene, Window window) {
        boundWindows.remove(window, scene);
        getSceneAccessor().getMutableWindowProperty(scene).set(null);
    }
}
