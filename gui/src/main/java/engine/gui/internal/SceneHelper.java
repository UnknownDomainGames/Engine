package engine.gui.internal;

import engine.gui.Scene;

public abstract class SceneHelper {

    private static SceneAccessor sceneAccessor;

    public static SceneAccessor getSceneAccessor() {
        return sceneAccessor;
    }

    public static void resize(Scene scene, float width, float height) {
        sceneAccessor.resize(scene, width, height);
    }

    public static void setSceneAccessor(SceneAccessor accessor) {
        if (sceneAccessor != null)
            throw new IllegalStateException("Cannot set twice");
        sceneAccessor = accessor;
    }

    public interface SceneAccessor {
        void resize(Scene scene, float width, float height);
    }
}
