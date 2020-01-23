package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.management.ResourceFactory;
import nullengine.client.rendering.texture.Texture2D;

public class GLResourceFactory implements ResourceFactory {
    private final Thread renderingThread;

    public GLResourceFactory(Thread renderingThread) {
        this.renderingThread = renderingThread;
    }

    @Override
    public Texture2D.Builder createTexture2DBuilder() {
        return GLTexture2D.builder();
    }
}
