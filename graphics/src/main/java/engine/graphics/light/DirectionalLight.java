package engine.graphics.light;

import engine.graphics.management.BindingProxy;
import org.joml.Vector3f;

public class DirectionalLight extends Light {
    private Vector3f direction;

    @Override
    public void bind(BindingProxy proxy, String fieldName) {
        proxy.setUniform(fieldName + ".filled", true);
        proxy.setUniform(fieldName + ".direction", direction);
        proxy.setUniform(fieldName + ".light.ambient", ambient);
        proxy.setUniform(fieldName + ".light.diffuse", diffuse);
        proxy.setUniform(fieldName + ".light.specular", specular);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public DirectionalLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public DirectionalLight setAmbient(Vector3f ambient) {
        super.setAmbient(ambient);
        return this;
    }

    @Override
    public DirectionalLight setDiffuse(Vector3f diffuse) {
        super.setDiffuse(diffuse);
        return this;
    }

    @Override
    public DirectionalLight setSpecular(Vector3f specular) {
        super.setSpecular(specular);
        return this;
    }
}
