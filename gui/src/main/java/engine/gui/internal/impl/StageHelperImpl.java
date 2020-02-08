package engine.gui.internal.impl;

import engine.graphics.GraphicsEngine;
import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.management.GraphicsBackend;
import engine.gui.Stage;
import engine.gui.internal.StageHelper;

import java.util.HashMap;
import java.util.Map;

public final class StageHelperImpl extends StageHelper {

    private final WindowHelper windowHelper;

    private final Map<Stage, StageInputHandler> stageInputHandlerMap = new HashMap<>();
    private final GUIRenderHandler renderHandler;

    private Stage primaryStage;
    private Window primaryWindow;

    public StageHelperImpl(GUIRenderHandler renderHandler) {
        this.renderHandler = renderHandler;
        GraphicsBackend renderManager = GraphicsEngine.getGraphicsBackend();
        windowHelper = renderManager.getWindowHelper();
        primaryStage = new Stage();
        primaryWindow = renderManager.getPrimaryWindow();
        setPrimary(primaryStage, true);
    }

    @Override
    public void show(Stage stage) {
        if (stage.isShowing()) throw new IllegalStateException("Stage is showing");
        Window window = getWindow(stage);
        if (window == null) {
            window = isPrimary(stage) ? primaryWindow : windowHelper.createWindow();
            setWindow(stage, window);
        }
        window.show();
        getShowingProperty(stage).set(true);
        doVisibleChanged(stage, true);
        enableInput(stage, window);
        enableRender(stage);
    }

    @Override
    public void hide(Stage stage) {
        if (!stage.isShowing()) throw new IllegalStateException("Stage is hiding");
        Window window = getWindow(stage);
        window.hide();
        disableRender(stage);
        disableInput(stage, window);
        getShowingProperty(stage).set(false);
        doVisibleChanged(stage, false);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void enableInput(Stage stage, Window window) {
        StageInputHandler stageInputHandler = new StageInputHandler(stage, window);
        stageInputHandler.enable();
        stageInputHandlerMap.put(stage, stageInputHandler);
    }

    public void disableInput(Stage stage, Window window) {
        stageInputHandlerMap.remove(stage).disable();
    }

    public void enableRender(Stage stage) {
        renderHandler.add(stage);
    }

    public void disableRender(Stage stage) {
        renderHandler.remove(stage);
    }
}
