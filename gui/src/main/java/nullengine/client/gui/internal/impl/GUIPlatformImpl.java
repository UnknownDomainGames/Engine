package nullengine.client.gui.internal.impl;

import nullengine.client.gui.Stage;
import nullengine.client.gui.application.GUIApplication;
import nullengine.client.gui.internal.GUIPlatform;
import nullengine.client.gui.internal.SceneHelper;
import nullengine.client.gui.internal.StageHelper;
import nullengine.client.gui.internal.impl.gl.GUIRenderPipeline;
import nullengine.client.gui.internal.impl.glfw.GLFWClipboardHelper;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.util.FrameTicker;

public final class GUIPlatformImpl extends GUIPlatform {

    private final StageHelperImpl stageHelper = new StageHelperImpl();
    private final SceneHelper sceneHelper = new SceneHelperImpl();

    private final ClipboardHelper clipboardHelper = new GLFWClipboardHelper();

    private final FrameTicker ticker = new FrameTicker(this::doRender);

    public static void launch(Class<? extends GUIApplication> clazz, String[] args) throws Exception {
        RenderEngine.start(new RenderEngine.Settings());
        GUIPlatformImpl platform = new GUIPlatformImpl();
        setInstance(platform);
        RenderManager renderManager = RenderEngine.getManager();
        renderManager.attachPipeline(new GUIRenderPipeline());
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
