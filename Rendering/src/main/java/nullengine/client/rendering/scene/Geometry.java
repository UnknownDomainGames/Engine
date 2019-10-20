package nullengine.client.rendering.scene;

import nullengine.client.rendering.gl.SingleBufferVAO;

public class Geometry extends Node {

    protected SingleBufferVAO vao;

    public Geometry(SingleBufferVAO vao) {
        this.vao = vao;
    }

    public SingleBufferVAO getVao() {
        return vao;
    }

    public void setVao(SingleBufferVAO vao) {
        this.vao = vao;
    }
}
