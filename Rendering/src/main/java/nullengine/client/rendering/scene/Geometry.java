package nullengine.client.rendering.scene;

import nullengine.client.rendering.gl.SingleBufferVAO;
import nullengine.client.rendering.layer.RenderLayer;
import nullengine.client.rendering.queue.RenderQueue;

public class Geometry extends Node {

    protected SingleBufferVAO vao;

    protected RenderLayer renderLayer;

    public Geometry(SingleBufferVAO vao) {
        this.vao = vao;
        scene().addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getRenderQueue().remove(this, renderLayer);
            }
            if (newValue != null) {
                newValue.getRenderQueue().add(this, renderLayer);
            }
        });
    }

    public SingleBufferVAO getVao() {
        return vao;
    }

    public void setVao(SingleBufferVAO vao) {
        this.vao = vao;
    }

    public RenderLayer getRenderLayer() {
        return renderLayer;
    }

    public void setRenderLayer(RenderLayer renderLayer) {
        scene.ifPresent(scene -> {
            RenderQueue renderQueue = scene.getRenderQueue();
            renderQueue.remove(this, this.renderLayer);
            renderQueue.add(this, renderLayer);
        });
        this.renderLayer = renderLayer;
    }
}
