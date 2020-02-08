package engine.graphics.internal.impl.gl;

import engine.graphics.GraphicsEngine;
import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.internal.Platform3D;
import engine.graphics.internal.ViewportHelper;
import engine.graphics.texture.FrameBuffer;

public class GLPlatform3D extends Platform3D {

    private final ViewportHelperImpl viewportHelper = new ViewportHelperImpl();

    public static void launch(String[] args) throws Exception {
        GLPlatform3D platform = new GLPlatform3D();
        setInstance(platform);
        GraphicsEngine.start(new GraphicsEngine.Settings());
        GraphicsEngine.getGraphicsBackend().attachHandler(new Scene3DRenderHandler(platform.viewportHelper.getViewports()));
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
