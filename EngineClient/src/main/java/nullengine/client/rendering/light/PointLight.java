package nullengine.client.rendering.light;

import nullengine.client.rendering.shader.ShaderManager;
import org.joml.Vector3f;

public class PointLight extends Light {
    Vector3f position;

    float kconstant = 1.0f;
    float klinear;
    float kquadratic;

    @Override
    public void bind(String fieldName) {
        ShaderManager.setUniform(fieldName + ".filled", true);
        ShaderManager.setUniform(fieldName + ".position", position);
        ShaderManager.setUniform(fieldName + ".constant", kconstant);
        ShaderManager.setUniform(fieldName + ".linear", klinear);
        ShaderManager.setUniform(fieldName + ".quadratic", kquadratic);
        ShaderManager.setUniform(fieldName + ".light.ambient", ambient);
        ShaderManager.setUniform(fieldName + ".light.diffuse", diffuse);
        ShaderManager.setUniform(fieldName + ".light.specular", specular);
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
