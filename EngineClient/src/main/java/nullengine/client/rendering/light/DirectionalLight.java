package nullengine.client.rendering.light;

import nullengine.client.rendering.shader.ShaderManager;
import org.joml.Vector3f;

public class DirectionalLight extends Light {
    Vector3f direction;

    @Override
    public void bind(String fieldName) {
        ShaderManager.setUniform(fieldName + ".filled", true);
        ShaderManager.setUniform(fieldName + ".direction", direction);
        ShaderManager.setUniform(fieldName + ".light.ambient", ambient);
        ShaderManager.setUniform(fieldName + ".light.diffuse", diffuse);
        ShaderManager.setUniform(fieldName + ".light.specular", specular);
    }

    public DirectionalLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }
}
