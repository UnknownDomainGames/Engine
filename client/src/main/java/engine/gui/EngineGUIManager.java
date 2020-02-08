package engine.gui;

import engine.graphics.display.Window;
import engine.util.UndoHistory;

public class EngineGUIManager implements GUIManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;

    private Window window;
    private Stage stage;

    private Scene showingScene;
    private UndoHistory<Scene> sceneHistory;

    public EngineGUIManager(Window window, Stage stage) {
        this.window = window;
        this.stage = stage;
        sceneHistory = new UndoHistory<>(MAX_SCENE_HISTORY);
    }

    @Override
    public void show(Scene scene) {
        pushToHistory();
        showingScene = scene;
        stage.setScene(scene);
        if (scene == null) {
            window.getCursor().disableCursor();
            return;
        }
        window.getCursor().showCursor();
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
