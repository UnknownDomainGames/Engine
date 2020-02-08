package engine.graphics.internal.impl.gl;

import engine.graphics.RenderEngine;
import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.internal.Platform3D;
import engine.graphics.internal.ViewportHelper;

public class GLPlatform3D extends Platform3D {

    private final ViewportHelperImpl viewportHelper = new ViewportHelperImpl();

    public static void launch(String[] args) throws Exception {
        GLPlatform3D platform = new GLPlatform3D();
        setInstance(platform);
        RenderEngine.start(new RenderEngine.Settings());
        RenderEngine.getManager().attachHandler(new Scene3DRenderHandler(platform.viewportHelper.getViewports()));
    }

    @Override
    public ViewportHelper getViewportHelper() {
        return viewportHelper;
    }

    @Override
    public FrameBuffer getDefaultFrameBuffer() {
        return GLFrameBuffer.getDefaultFrameBuffer();
    }
}
