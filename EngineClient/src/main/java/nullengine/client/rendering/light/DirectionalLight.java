package nullengine.client.rendering.light;

import nullengine.client.rendering.shader.ShaderManager;
import org.joml.Vector3f;

public class DirectionalLight extends Light {
    Vector3f direction;

    @Override
    public void bind(String fieldName) {
        ShaderManager.instance().setUniform(fieldName + ".filled", true);
        ShaderManager.instance().setUniform(fieldName + ".direction", direction);
        ShaderManager.instance().setUniform(fieldName + ".light.ambient", ambient);
        ShaderManager.instance().setUniform(fieldName + ".light.diffuse", diffuse);
        ShaderManager.instance().setUniform(fieldName + ".light.specular", specular);
    }

    public DirectionalLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }
}
