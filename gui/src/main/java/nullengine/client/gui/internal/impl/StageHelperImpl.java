package nullengine.client.gui.internal.impl;

import nullengine.client.gui.Stage;
import nullengine.client.gui.internal.StageHelper;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.WindowHelper;
import nullengine.client.rendering.management.RenderManager;

import java.util.HashMap;
import java.util.Map;

public final class StageHelperImpl extends StageHelper {

    private final WindowHelper windowHelper;

    private final Map<Stage, StageInputHandler> stageInputHandlerMap = new HashMap<>();

    private Stage primaryStage;
    private Window primaryWindow;

    public StageHelperImpl() {
        RenderManager renderManager = RenderEngine.getManager();
        windowHelper = renderManager.getWindowHelper();
        primaryStage = new Stage();
        primaryWindow = renderManager.getPrimaryWindow();
        setPrimary(primaryStage, true);
    }

    @Override
    public void show(Stage stage) {
        Window window = isPrimary(stage) ? primaryWindow : windowHelper.createWindow();
        setWindow(stage, window);
        window.show();
        getShowingProperty(stage).set(true);
        StageInputHandler stageInputHandler = new StageInputHandler(stage, window);
        stageInputHandler.enable();
        stageInputHandlerMap.put(stage, stageInputHandler);
        doVisibleChanged(stage, true);
    }

    @Override
    public void hide(Stage stage) {
        Window window = getWindow(stage);
        window.hide();
        getShowingProperty(stage).set(false);
        doVisibleChanged(stage, false);
        stageInputHandlerMap.remove(stage).disable();
        setWindow(stage, null);
        window.dispose();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
