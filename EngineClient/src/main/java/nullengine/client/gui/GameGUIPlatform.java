package nullengine.client.gui;

import nullengine.Platform;
import nullengine.client.gui.internal.ClipboardHelper;
import nullengine.client.gui.internal.GUIPlatform;
import nullengine.client.gui.internal.SceneHelper;
import nullengine.client.gui.internal.StageHelper;
import nullengine.client.gui.internal.impl.GUIRenderHandler;
import nullengine.client.gui.internal.impl.SceneHelperImpl;
import nullengine.client.gui.internal.impl.StageHelperImpl;
import nullengine.client.gui.internal.impl.glfw.GLFWClipboardHelper;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.management.RenderManager;

import static nullengine.client.gui.internal.SceneHelper.getViewportHeight;
import static nullengine.client.gui.internal.SceneHelper.getViewportWidth;
import static nullengine.client.gui.internal.StageHelper.getWindow;

public final class GameGUIPlatform extends GUIPlatform {

    private final GUIRenderHandler renderHandler = new GUIRenderHandler();
    private final StageHelperImpl stageHelper = new StageHelperImpl(renderHandler);
    private final SceneHelper sceneHelper = new SceneHelperImpl();
    private final ClipboardHelper clipboardHelper = new GLFWClipboardHelper();

    private final Window primaryWindow = RenderEngine.getManager().getPrimaryWindow();

    private Stage guiStage;
    private Stage hudStage;

    public GameGUIPlatform() {
        setInstance(this);
        RenderManager renderManager = RenderEngine.getManager();
        renderManager.attachHandler(renderHandler);
        Stage.getStages().addChangeListener(change -> {
            if (change.getList().isEmpty()) Platform.getEngine().terminate();
        });
        initGUIStage();
        initHUDStage();
    }

    private void initGUIStage() {
        Stage stage = stageHelper.getPrimaryStage();
        StageHelper.setWindow(stage, primaryWindow);
        StageHelper.getShowingProperty(stage).set(true);
        StageHelper.doVisibleChanged(stage, true);
        stageHelper.enableInput(stage, primaryWindow);
        guiStage = stage;
    }

    private void initHUDStage() {
        Stage stage = new Stage();
        StageHelper.setPrimary(stage, true);
        StageHelper.setWindow(stage, primaryWindow);
        StageHelper.getShowingProperty(stage).set(true);
        StageHelper.doVisibleChanged(stage, true);
        hudStage = stage;
    }

    public Stage getGUIStage() {
        return guiStage;
    }

    public Stage getHUDStage() {
        return hudStage;
    }

    public void doRender() {
        render(guiStage);
        render(hudStage);
        primaryWindow.swapBuffers();
    }

    private void render(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null) return;

        Window window = getWindow(stage);
        if (getViewportWidth(scene) != window.getWidth() ||
                getViewportHeight(scene) != window.getHeight()) {
            SceneHelper.setViewport(scene, window.getWidth(), window.getHeight(),
                    window.getContentScaleX(), window.getContentScaleY());
        }

        scene.update();
        renderHandler.getRenderer().render(scene, false);
    }

    @Override
    public StageHelper getStageHelper() {
        return stageHelper;
    }

    @Override
    public SceneHelper getSceneHelper() {
        return sceneHelper;
    }

    @Override
    public ClipboardHelper getClipboardHelper() {
        return clipboardHelper;
    }

    public void dispose() {
    }
}
