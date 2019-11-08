package nullengine.client.rendering.scene.light;

import org.joml.Vector3f;

public class DirectionalLight extends Light {
    private Vector3f direction;

    @Override
    public void bind(String fieldName) {
//        ShaderManager.instance().setUniform(fieldName + ".filled", true);
//        ShaderManager.instance().setUniform(fieldName + ".direction", direction);
//        ShaderManager.instance().setUniform(fieldName + ".light.ambient", ambient);
//        ShaderManager.instance().setUniform(fieldName + ".light.diffuse", diffuse);
//        ShaderManager.instance().setUniform(fieldName + ".light.specular", specular);
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
