package engine.graphics.internal.impl;

import engine.graphics.GraphicsEngine;
import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.internal.Platform3D;
import engine.graphics.internal.ViewportHelper;
import engine.graphics.texture.FrameBuffer;

public class Platform3DImpl extends Platform3D {

    private final ViewportHelperImpl viewportHelper = new ViewportHelperImpl();

    public static void launch(String... args) {
        Platform3DImpl platform = new Platform3DImpl();
        setInstance(platform);
        GraphicsEngine.start(new GraphicsEngine.Settings());
    }

    public static void launchEmbedded(String... args) {
        Platform3DImpl platform = new Platform3DImpl();
        setInstance(platform);
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
