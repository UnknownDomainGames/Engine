package nullengine.client.rendering.gl;

import nullengine.client.rendering.management.ResourceFactory;

public class GLResourceFactory implements ResourceFactory {
    private final Thread renderingThread;

    public GLResourceFactory(Thread renderingThread) {
        this.renderingThread = renderingThread;
    }
}
