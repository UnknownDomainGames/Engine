package engine.graphics.internal.impl.vk;

import engine.graphics.GraphicsEngine;
import engine.graphics.internal.Platform3D;
import engine.graphics.internal.ViewportHelper;
import engine.graphics.internal.impl.gl.ViewportHelperImpl;
import engine.graphics.texture.FrameBuffer;

public class VKPlatform3D extends Platform3D {

    private final ViewportHelperImpl viewportHelper = new ViewportHelperImpl();

    public static void launch(String... args) {
        VKPlatform3D platform = new VKPlatform3D();
        setInstance(platform);
        GraphicsEngine.start(new GraphicsEngine.Settings());
//        GraphicsEngine.getGraphicsBackend().attachHandler(new Scene3DRenderHandler(platform.viewportHelper.getViewports()));
    }

    public static void launchEmbedded(String... args) {
        VKPlatform3D platform = new VKPlatform3D();
        setInstance(platform);
//        GraphicsEngine.getGraphicsBackend().attachHandler(new Scene3DRenderHandler(platform.viewportHelper.getViewports()));
    }

    @Override
    public ViewportHelper getViewportHelper() {
        return viewportHelper;
    }

    @Override
    public FrameBuffer getDefaultFrameBuffer() {
        return null;
    }
}
