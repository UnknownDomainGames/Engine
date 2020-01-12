package nullengine.client.gui.internal.impl;

import nullengine.client.gui.Stage;
import nullengine.client.gui.internal.StageHelper;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.WindowHelper;
import nullengine.client.rendering.management.RenderManager;

public class StageHelperImpl extends StageHelper {

    private final WindowHelper windowHelper;

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
        doVisibleChanged(stage, true);
    }

    @Override
    public void hide(Stage stage) {
        Window window = getWindow(stage);
        window.hide();
        getShowingProperty(stage).set(false);
        doVisibleChanged(stage, false);
        setWindow(stage, null);
        window.dispose();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
