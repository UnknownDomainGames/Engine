package nullengine.client.rendering.scene;

import nullengine.client.rendering.queue.RenderQueue;
import nullengine.client.rendering.scene.light.LightManager;

public class Scene extends Node {

    private final RenderQueue renderQueue = new RenderQueue();
    private final LightManager lightManager = new LightManager();

    public Scene() {
        scene.set(this);
    }

    public RenderQueue getRenderQueue() {
        return renderQueue;
    }

    public LightManager getLightManager() {
        return lightManager;
    }
}
