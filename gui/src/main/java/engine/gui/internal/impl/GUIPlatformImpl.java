package engine.gui.internal.impl;

import engine.gui.Stage;
import engine.gui.application.GUIApplication;
import engine.gui.internal.ClipboardHelper;
import engine.gui.internal.GUIPlatform;
import engine.gui.internal.SceneHelper;
import engine.gui.internal.StageHelper;
import engine.gui.internal.impl.glfw.GLFWClipboardHelper;
import engine.graphics.RenderEngine;
import engine.graphics.management.RenderManager;
import engine.graphics.util.FrameTicker;

public final class GUIPlatformImpl extends GUIPlatform {

    private final GUIRenderHandler renderHandler = new GUIRenderHandler();

    private final StageHelperImpl stageHelper = new StageHelperImpl(renderHandler);
    private final SceneHelper sceneHelper = new SceneHelperImpl();

    private final ClipboardHelper clipboardHelper = new GLFWClipboardHelper();

    private final FrameTicker ticker = new FrameTicker(this::doRender);

    public static void launch(Class<? extends GUIApplication> clazz, String[] args) throws Exception {
        RenderEngine.start(new RenderEngine.Settings());
        GUIPlatformImpl platform = new GUIPlatformImpl();
        setInstance(platform);
        RenderManager renderManager = RenderEngine.getManager();
        renderManager.attachHandler(platform.renderHandler);
        Stage.getStages().addChangeListener(change -> {
            if (change.getList().isEmpty()) platform.ticker.stop();
        });
        GUIApplication application = clazz.getConstructor().newInstance();
        application.start(platform.stageHelper.getPrimaryStage());
        platform.ticker.run();
    }

    private GUIPlatformImpl() {
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

    private void doRender() {
        RenderEngine.doRender(ticker.getTpf());
    }
}
