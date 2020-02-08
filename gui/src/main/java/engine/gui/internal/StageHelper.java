package engine.gui.internal;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import engine.gui.Stage;
import engine.graphics.display.Window;

public abstract class StageHelper {

    private static StageAccessor stageAccessor;

    public static StageAccessor getStageAccessor() {
        return stageAccessor;
    }

    public static void setStageAccessor(StageAccessor accessor) {
        if (stageAccessor != null) {
            throw new IllegalStateException("Cannot set twice");
        }
        stageAccessor = accessor;
    }

    public static Window getWindow(Stage stage) {
        return stageAccessor.getWindow(stage);
    }

    public static void setWindow(Stage stage, Window window) {
        stageAccessor.setWindow(stage, window);
    }

    public static boolean isPrimary(Stage stage) {
        return stageAccessor.isPrimary(stage);
    }

    public static void setPrimary(Stage stage, boolean primary) {
        stageAccessor.setPrimary(stage, primary);
    }

    public static MutableBooleanValue getShowingProperty(Stage stage) {
        return stageAccessor.getShowingProperty(stage);
    }

    public static void doVisibleChanged(Stage stage, boolean value) {
        stageAccessor.doVisibleChanged(stage, value);
    }

    public abstract void show(Stage stage);

    public abstract void hide(Stage stage);

    public interface StageAccessor {
        Window getWindow(Stage stage);

        void setWindow(Stage stage, Window window);

        boolean isPrimary(Stage stage);

        void setPrimary(Stage stage, boolean primary);

        MutableBooleanValue getShowingProperty(Stage stage);

        void doVisibleChanged(Stage stage, boolean value);
    }
}
