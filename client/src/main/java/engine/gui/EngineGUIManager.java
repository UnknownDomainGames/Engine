package engine.gui;

import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.util.UndoHistory;

public class EngineGUIManager implements GUIManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;

    private Window window;
    private Stage stage;
    private HUDManager hudManager;

    private Scene showingScene;
    private UndoHistory<Scene> sceneHistory;

    public EngineGUIManager(Window window, Stage stage, HUDManager hudManager) {
        this.window = window;
        this.stage = stage;
        this.hudManager = hudManager;
        sceneHistory = new UndoHistory<>(MAX_SCENE_HISTORY);
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
