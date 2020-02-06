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
    private final GUIRenderHandler renderHandler;

    private Stage primaryStage;
    private Window primaryWindow;

    public StageHelperImpl(GUIRenderHandler renderHandler) {
        this.renderHandler = renderHandler;
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
        doVisibleChanged(stage, true);
        enableInput(stage, window);
        enableRender(stage);
    }

    @Override
    public void hide(Stage stage) {
        Window window = getWindow(stage);
        window.hide();
        disableRender(stage);
        disableInput(stage, window);
        getShowingProperty(stage).set(false);
        doVisibleChanged(stage, false);
        setWindow(stage, null);
        window.dispose();
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
