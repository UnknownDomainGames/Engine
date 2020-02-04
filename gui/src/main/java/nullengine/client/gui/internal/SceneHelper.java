package nullengine.client.gui.internal;

import nullengine.client.gui.Scene;

public abstract class SceneHelper {

    private static SceneAccessor sceneAccessor;

    public static SceneAccessor getSceneAccessor() {
        return sceneAccessor;
    }

    public static int getViewportWidth(Scene scene) {
        return sceneAccessor.getViewportWidth(scene);
    }

    public static int getViewportHeight(Scene scene) {
        return sceneAccessor.getViewportHeight(scene);
    }

    public static void setViewport(Scene scene, int width, int height, float scaleX, float scaleY) {
        sceneAccessor.setViewport(scene, width, height, scaleX, scaleY);
    }

    public static void setSceneAccessor(SceneAccessor accessor) {
        if (sceneAccessor != null)
            throw new IllegalStateException("Cannot set twice");
        sceneAccessor = accessor;
    }

    public interface SceneAccessor {
        int getViewportWidth(Scene scene);

        int getViewportHeight(Scene scene);

        void setViewport(Scene scene, int width, int height, float scaleX, float scaleY);
    }
}
