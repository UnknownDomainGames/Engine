package nullengine.client.rendering.scene.light;

import org.joml.Vector3f;

public class PointLight extends Light {
    private Vector3f position;

    private float kconstant = 1.0f;
    private float klinear;
    private float kquadratic;

    @Override
    public void bind(String fieldName) {
//        ShaderManager.instance().setUniform(fieldName + ".filled", true);
//        ShaderManager.instance().setUniform(fieldName + ".position", position);
//        ShaderManager.instance().setUniform(fieldName + ".constant", kconstant);
//        ShaderManager.instance().setUniform(fieldName + ".linear", klinear);
//        ShaderManager.instance().setUniform(fieldName + ".quadratic", kquadratic);
//        ShaderManager.instance().setUniform(fieldName + ".light.ambient", ambient);
//        ShaderManager.instance().setUniform(fieldName + ".light.diffuse", diffuse);
//        ShaderManager.instance().setUniform(fieldName + ".light.specular", specular);
    }

    public Vector3f getPosition() {
        return position;
    }

    public PointLight setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public float getKconstant() {
        return kconstant;
    }

    public PointLight setKconstant(float kconstant) {
        this.kconstant = kconstant;
        return this;
    }

    public float getKlinear() {
        return klinear;
    }

    public PointLight setKlinear(float klinear) {
        this.klinear = klinear;
        return this;
    }

    public float getKquadratic() {
        return kquadratic;
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
