package nullengine.client.rendering3d;

import nullengine.client.rendering.math.BoundingVolume;
import nullengine.client.rendering.scene.Renderable;
import nullengine.client.rendering3d.queue.RenderQueue;
import nullengine.client.rendering3d.queue.RenderType;
import nullengine.client.rendering3d.queue.StandardRenderTypes;

public class Geometry extends Node3D {

    private Renderable renderable;
    private RenderType renderType;

    private BoundingVolume boundingVolume = new BoundingVolume();

    public Geometry() {
        this(null, StandardRenderTypes.OPAQUE);
    }

    public Geometry(Renderable renderable) {
        this(renderable, StandardRenderTypes.OPAQUE);
    }

    public Geometry(Renderable renderable, RenderType renderType) {
        this.renderable = renderable;
        this.renderType = renderType;
        scene().addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getRenderQueue().remove(this, renderType);
            }
            if (newValue != null) {
                newValue.getRenderQueue().add(this, renderType);
            }
        });
    }

    public Renderable getRenderable() {
        return renderable;
    }

    public void setRenderable(Renderable renderable) {
        this.renderable = renderable;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        scene.ifPresent(scene -> {
            RenderQueue renderQueue = scene.getRenderQueue();
            renderQueue.remove(this, this.renderType);
            renderQueue.add(this, renderType);
        });
        this.renderType = renderType;
    }

    public BoundingVolume getBoundingVolume() {
        return boundingVolume;
    }
}
