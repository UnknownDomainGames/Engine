package engine.graphics;

import engine.graphics.math.BoundingVolume;
import engine.graphics.queue.RenderQueue;
import engine.graphics.queue.RenderType;
import engine.graphics.queue.StandardRenderTypes;

public class Geometry extends Node3D {

    private Drawable drawable;
    private RenderType renderType;

    private BoundingVolume boundingVolume = new BoundingVolume();

    public Geometry() {
        this(null, StandardRenderTypes.OPAQUE);
    }

    public Geometry(Drawable drawable) {
        this(drawable, StandardRenderTypes.OPAQUE);
    }

    public Geometry(Drawable drawable, RenderType renderType) {
        this.drawable = drawable;
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

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
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
