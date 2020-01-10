package nullengine.client.gui.internal.impl;

import nullengine.client.gui.application.UIApplication;
import nullengine.client.gui.internal.GUIPlatform;
import nullengine.client.gui.internal.SceneHelper;
import nullengine.client.gui.internal.impl.gl.GUIRenderPipeline;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.util.FrameTicker;

public final class GUIPlatformImpl extends GUIPlatform {

    private final SceneHelperImpl sceneHelper = new SceneHelperImpl();

    private final FrameTicker ticker = new FrameTicker(this::doRender);

    public static void launch(Class<? extends UIApplication> clazz, String[] args) throws Exception {
        GUIPlatformImpl platform = new GUIPlatformImpl();
        setInstance(platform);
        RenderEngine.start(new RenderEngine.Settings());
        RenderManager renderManager = RenderEngine.getManager();
        renderManager.attachPipeline(new GUIRenderPipeline(platform.sceneHelper.boundWindows));
        UIApplication application = clazz.getConstructor().newInstance();
        application.start(renderManager.getPrimaryWindow());
        platform.ticker.run();
    }

    private GUIPlatformImpl() {
    }

    @Override
    public SceneHelper getSceneHelper() {
        return sceneHelper;
    }

    private void doRender() {
        RenderEngine.doRender(ticker.getTpf());
    }
}
