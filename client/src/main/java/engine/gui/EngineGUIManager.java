package engine.gui;

import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.gui.stage.Stage;
import engine.util.UndoHistory;

public final class EngineGUIManager implements GUIManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;

    private final Window window;
    private final Stage stage;
    private final HUDManager hudManager;

    private final UndoHistory<Scene> sceneHistory = new UndoHistory<>(MAX_SCENE_HISTORY);

    private Scene showingScene;

    public EngineGUIManager(Window window, Stage stage, HUDManager hudManager) {
        this.window = window;
        this.stage = stage;
        this.hudManager = hudManager;
    }

    @Override
    public float getScaleX() {
        return stage.getUserScaleX();
    }

    @Override
    public float getScaleY() {
        return stage.getUserScaleY();
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        stage.setUserScale(scaleX, scaleY);
    }

    @Override
    public void show(Scene scene) {
        pushToHistory();
        showingScene = scene;
        stage.setScene(scene);
        if (scene == null) {
            hudManager.setVisible(true);
            window.getCursor().disableCursor();
        } else {
            hudManager.setVisible(false);
            window.getCursor().showCursor();
        }
    }

    private void pushToHistory() {
        if (showingScene == null) {
            return;
        }
        sceneHistory.pushHistory(showingScene);
    }

    @Override
    public void showLast() {
        show(sceneHistory.undo());
    }

    @Override
    public void close() {
        show(null);
    }

    @Override
    public Scene getShowingScene() {
        return showingScene;
    }

    @Override
    public boolean isShowing() {
        return showingScene != null;
    }
}
