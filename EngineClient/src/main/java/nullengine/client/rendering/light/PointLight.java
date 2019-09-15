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
        ShaderManager.instance().setUniform(fieldName + ".filled", true);
        ShaderManager.instance().setUniform(fieldName + ".position", position);
        ShaderManager.instance().setUniform(fieldName + ".constant", kconstant);
        ShaderManager.instance().setUniform(fieldName + ".linear", klinear);
        ShaderManager.instance().setUniform(fieldName + ".quadratic", kquadratic);
        ShaderManager.instance().setUniform(fieldName + ".light.ambient", ambient);
        ShaderManager.instance().setUniform(fieldName + ".light.diffuse", diffuse);
        ShaderManager.instance().setUniform(fieldName + ".light.specular", specular);
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

    @Override
    public PointLight setAmbient(Vector3f ambient) {
        super.setAmbient(ambient);
        return this;
    }

    @Override
    public PointLight setDiffuse(Vector3f diffuse) {
        super.setDiffuse(diffuse);
        return this;
    }

    @Override
    public PointLight setSpecular(Vector3f specular) {
        super.setSpecular(specular);
        return this;
    }
}
