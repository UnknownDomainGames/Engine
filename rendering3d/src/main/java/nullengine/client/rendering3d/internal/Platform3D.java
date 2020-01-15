package nullengine.client.rendering3d.internal;

import nullengine.client.rendering.texture.FrameBuffer;

public abstract class Platform3D {

    private static Platform3D PLATFORM;

    public static Platform3D getInstance() {
        return PLATFORM;
    }

    protected static void setInstance(Platform3D platform) {
        PLATFORM = platform;
    }

    public abstract ViewportHelper getViewportHelper();

    public abstract FrameBuffer getDefaultFrameBuffer();
}
