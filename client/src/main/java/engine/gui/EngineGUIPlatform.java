package engine.gui;

import engine.Platform;
import engine.graphics.GraphicsEngine;
import engine.graphics.display.Window;
import engine.gui.internal.*;
import engine.gui.internal.impl.InputHelperImpl;
import engine.gui.internal.impl.SceneHelperImpl;
import engine.gui.internal.impl.StageHelperImpl;
import engine.gui.internal.impl.glfw.GLFWClipboardHelper;
import engine.gui.internal.impl.lwjgl.TinyFDFileChooserHelper;
import engine.gui.stage.Stage;

public final class EngineGUIPlatform extends GUIPlatform {

    private static EngineGUIPlatform PLATFORM;

    private final StageHelperImpl stageHelper = new StageHelperImpl();
    private final SceneHelper sceneHelper = new SceneHelperImpl();
    private final ClipboardHelper clipboardHelper = new GLFWClipboardHelper();
    private final FileChooserHelper fileChooserHelper = new TinyFDFileChooserHelper();

    private final Window primaryWindow = GraphicsEngine.getGraphicsBackend().getPrimaryWindow();

    private Stage guiStage;
    private Stage hudStage;

    public static EngineGUIPlatform getInstance() {
        return PLATFORM;
    }

    public EngineGUIPlatform() {
        setInstance(this);
        PLATFORM = this;
        InputHelperImpl.initialize();
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

    @Override
    public StageHelperImpl getStageHelper() {
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

    @Override
    public FileChooserHelper getFileChooserHelper() {
        return fileChooserHelper;
    }

    public void dispose() {
    }
}
