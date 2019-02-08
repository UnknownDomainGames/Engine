package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;
import unknowndomain.engine.client.rendering.shader.ShaderManager;

public class DirectionalLight extends Light {
    Vector3f direction;

    @Override
    public void bind(String fieldName) {
        ShaderManager.INSTANCE.setUniform(fieldName + ".filled", true);
        ShaderManager.INSTANCE.setUniform(fieldName + ".direction", direction);
        ShaderManager.INSTANCE.setUniform(fieldName + ".light.ambient", ambient);
        ShaderManager.INSTANCE.setUniform(fieldName + ".light.diffuse", diffuse);
        ShaderManager.INSTANCE.setUniform(fieldName + ".light.specular", specular);
    }

    public DirectionalLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }
}
