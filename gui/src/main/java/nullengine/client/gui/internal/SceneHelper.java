package nullengine.client.gui.internal;

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

    public interface SceneAccessor {
    }
}
