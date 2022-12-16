package engine.gui.internal;

import engine.gui.Scene;
import engine.gui.stage.Stage;

public abstract class SceneHelper {

    private static SceneAccessor sceneAccessor;

    public static SceneAccessor getSceneAccessor() {
        return sceneAccessor;
    }

    public static void setStage(Scene scene, Stage stage) {
        sceneAccessor.setStage(scene, stage);
    }

    public static void setSize(Scene scene, double width, double height) {
        sceneAccessor.setSize(scene, width, height);
    }

    public static void preferredSize(Scene scene) {
        sceneAccessor.preferredSize(scene);
    }

    public static void setSceneAccessor(SceneAccessor accessor) {
        if (sceneAccessor != null)
            throw new IllegalStateException("Cannot set twice");
        sceneAccessor = accessor;
    }

    public interface SceneAccessor {
        void setStage(Scene scene, Stage stage);

        void setSize(Scene scene, double width, double height);

        void preferredSize(Scene scene);
    }
}
