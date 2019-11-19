package nullengine.client.rendering.scene;

import nullengine.client.rendering.gl.SingleBufferVAO;
import nullengine.client.rendering.graphics.RenderType;
import nullengine.client.rendering.math.BoundingVolume;
import nullengine.client.rendering.queue.RenderQueue;

public class Geometry extends Node {

    private SingleBufferVAO vao;
    private RenderType renderType;

    private BoundingVolume boundingVolume = new BoundingVolume();

    public Geometry(SingleBufferVAO vao) {
        this.vao = vao;
        scene().addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getRenderQueue().remove(this, renderType);
            }
            if (newValue != null) {
                newValue.getRenderQueue().add(this, renderType);
            }
        });
    }

    public SingleBufferVAO getVao() {
        return vao;
    }

    public void setVao(SingleBufferVAO vao) {
        this.vao = vao;
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
