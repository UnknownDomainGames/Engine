package nullengine.client.rendering.scene;

import nullengine.client.rendering.queue.RenderQueue;

public class Scene extends Node {

    private final RenderQueue renderQueue = new RenderQueue();

    public Scene() {
        scene.set(this);
    }

    public RenderQueue getRenderQueue() {
        return renderQueue;
    }
}
