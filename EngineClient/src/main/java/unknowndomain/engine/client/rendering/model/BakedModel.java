package unknowndomain.engine.client.rendering.model;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.client.rendering.texture.GLTexture;

public class BakedModel {
    private GLMesh mesh;
    private GLTexture texture;

    private Vector3f translate = new Vector3f();
    private Quaternionf rotation = new Quaternionf();

    public BakedModel(GLMesh mesh, GLTexture texture) {
        this.mesh = mesh;
        this.texture = texture;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getTranslate() {
        return translate;
    }

    public void render() {
        texture.bind();
        mesh.render();
    }

    public Matrix4f getTransform() {
        return new Matrix4f().setTranslation(translate).rotate(rotation);
    }
}
