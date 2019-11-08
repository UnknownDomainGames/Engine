package nullengine.client.rendering.scene.light;

import org.joml.Vector3f;

public class SpotLight extends Light {
    private Vector3f position;

    private float kconstant = 1.0f;
    private float klinear;
    private float kquadratic;

    private Vector3f direction;
    private float cutoffAngle; // in Radian
    private float outerCutoffAngle; // in Radian

    @Override
    public void bind(String fieldName) {
//        super.bind(fieldName);
//        ShaderManager.instance().setUniform(fieldName + ".direction", direction);
//        ShaderManager.instance().setUniform(fieldName + ".cutoffCosine", (float) Math.cos(cutoffAngle));
//        ShaderManager.instance().setUniform(fieldName + ".direction", (float) Math.cos(outerCutoffAngle));
    }

    public Vector3f getPosition() {
        return position;
    }

    public SpotLight setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public float getKconstant() {
        return kconstant;
    }

    public SpotLight setKconstant(float kconstant) {
        this.kconstant = kconstant;
        return this;
    }

    public float getKlinear() {
        return klinear;
    }

    public SpotLight setKlinear(float klinear) {
        this.klinear = klinear;
        return this;
    }

    public float getKquadratic() {
        return kquadratic;
    }

    public SpotLight setKquadratic(float kquadratic) {
        this.kquadratic = kquadratic;
        return this;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public SpotLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }

    public float getCutoffAngle() {
        return cutoffAngle;
    }

    public SpotLight setCutoffAngle(float cutoffAngle) {
        this.cutoffAngle = cutoffAngle;
        return this;
    }

    public float getOuterCutoffAngle() {
        return outerCutoffAngle;
    }

    public SpotLight setOuterCutoffAngle(float outerCutoffAngle) {
        this.outerCutoffAngle = outerCutoffAngle;
        return this;
    }

    @Override
    public SpotLight setAmbient(Vector3f ambient) {
        super.setAmbient(ambient);
        return this;
    }

    @Override
    public SpotLight setDiffuse(Vector3f diffuse) {
        super.setDiffuse(diffuse);
        return this;
    }

    @Override
    public SpotLight setSpecular(Vector3f specular) {
        super.setSpecular(specular);
        return this;
    }
}
