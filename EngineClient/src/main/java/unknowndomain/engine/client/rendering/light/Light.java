package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;

public abstract class Light {
    Vector3f ambient = new Vector3f(0.1f);
    Vector3f diffuse = new Vector3f(1f);
    Vector3f specular = new Vector3f(1f);

    public abstract void bind(String fieldName);

    public Light setAmbient(Vector3f ambient) {
        this.ambient = ambient;
        return this;
    }

    public Light setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public Light setSpecular(Vector3f specular) {
        this.specular = specular;
        return this;
    }
}
