package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;

public class DirectionalLight extends Light {
    Vector3f direction;

    @Override
    public void bind(ShaderProgram program, String fieldName) {
        program.setUniform(fieldName + ".direction", direction);
        program.setUniform(fieldName + ".light.ambient", ambient);
        program.setUniform(fieldName + ".light.diffuse", diffuse);
        program.setUniform(fieldName + ".light.specular", specular);
    }

    public DirectionalLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }
}
