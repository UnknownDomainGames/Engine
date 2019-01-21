package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;

public class PointLight extends Light {
    Vector3f position;

    float kconstant = 1.0f;
    float klinear;
    float kquadratic;

    @Override
    public void bind(ShaderProgram program, String fieldName) {
        program.setUniform(fieldName + ".filled", true);
        program.setUniform(fieldName + ".position", position);
        program.setUniform(fieldName + ".constant", kconstant);
        program.setUniform(fieldName + ".linear", klinear);
        program.setUniform(fieldName + ".quadratic", kquadratic);
        program.setUniform(fieldName + ".light.ambient", ambient);
        program.setUniform(fieldName + ".light.diffuse", diffuse);
        program.setUniform(fieldName + ".light.specular", specular);
    }

    public PointLight setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public PointLight setKconstant(float kconstant) {
        this.kconstant = kconstant;
        return this;
    }

    public PointLight setKlinear(float klinear) {
        this.klinear = klinear;
        return this;
    }

    public PointLight setKquadratic(float kquadratic) {
        this.kquadratic = kquadratic;
        return this;
    }
}
