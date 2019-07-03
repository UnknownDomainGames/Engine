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
        ShaderManager.INSTANCE.setUniform(fieldName + ".filled", true);
        ShaderManager.INSTANCE.setUniform(fieldName + ".position", position);
        ShaderManager.INSTANCE.setUniform(fieldName + ".constant", kconstant);
        ShaderManager.INSTANCE.setUniform(fieldName + ".linear", klinear);
        ShaderManager.INSTANCE.setUniform(fieldName + ".quadratic", kquadratic);
        ShaderManager.INSTANCE.setUniform(fieldName + ".light.ambient", ambient);
        ShaderManager.INSTANCE.setUniform(fieldName + ".light.diffuse", diffuse);
        ShaderManager.INSTANCE.setUniform(fieldName + ".light.specular", specular);
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
