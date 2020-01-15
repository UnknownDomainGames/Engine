package nullengine.client.rendering3d.internal.impl.gl;

import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.gl.texture.GLFrameBuffer;
import nullengine.client.rendering.texture.FrameBuffer;
import nullengine.client.rendering3d.internal.Platform3D;
import nullengine.client.rendering3d.internal.ViewportHelper;

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
