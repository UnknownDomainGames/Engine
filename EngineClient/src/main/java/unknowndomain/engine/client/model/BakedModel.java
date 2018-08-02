package unknowndomain.engine.client.model;

import org.joml.Matrix4f;
import unknowndomain.engine.client.texture.GLTexture;

public class BakedModel {
    private GLMesh mesh;
    private GLTexture texture;

    private Matrix4f translate = new Matrix4f();
    private Matrix4f rotation = new Matrix4f();

    public BakedModel(GLMesh mesh, GLTexture texture) {
        this.mesh = mesh;
        this.texture = texture;
    }

    public Matrix4f getRotation() {
        return rotation;
    }

    public Matrix4f getTranslate() {
        return translate;
    }

    public void render() {
        texture.bind();
        mesh.render();
    }

    public Matrix4f getTransform() {
        return new Matrix4f(translate).mul(rotation);
    }
}
