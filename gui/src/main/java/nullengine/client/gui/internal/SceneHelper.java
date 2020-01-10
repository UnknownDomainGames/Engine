package nullengine.client.gui.internal;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import nullengine.client.gui.Scene;
import nullengine.client.rendering.display.Window;

public abstract class SceneHelper {

    private static SceneAccessor sceneAccessor;

    public static SceneAccessor getSceneAccessor() {
        return sceneAccessor;
    }

    public static void setSceneAccessor(SceneAccessor accessor) {
        if (sceneAccessor != null)
            throw new IllegalStateException("Cannot set twice");
        sceneAccessor = accessor;
    }

    public abstract void bindWindow(Scene scene, Window window);

    public abstract void unbindWindow(Scene scene, Window window);

    public interface SceneAccessor {
        MutableObjectValue<Window> getMutableWindowProperty(Scene scene);
    }
}
