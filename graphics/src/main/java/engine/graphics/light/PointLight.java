package engine.graphics.light;

import engine.graphics.shader.ShaderResource;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class PointLight extends Light {
    private Vector3f position;

    private float kconstant = 1.0f;
    private float klinear;
    private float kquadratic;

    public void bind(ShaderResource proxy, String fieldName) {
        proxy.setUniform(fieldName + ".filled", true);
        proxy.setUniform(fieldName + ".position", position);
        proxy.setUniform(fieldName + ".constant", kconstant);
        proxy.setUniform(fieldName + ".linear", klinear);
        proxy.setUniform(fieldName + ".quadratic", kquadratic);
        proxy.setUniform(fieldName + ".light.ambient", ambient);
        proxy.setUniform(fieldName + ".light.diffuse", diffuse);
        proxy.setUniform(fieldName + ".light.specular", specular);
    }

    @Override
    public ByteBuffer get(MemoryStack stack) {
        return get(stack.malloc(20 * Float.BYTES));
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        buffer.putInt(index, 1);
        position.get(index + 1, buffer);
        buffer.putFloat(index + 5, kconstant);
        buffer.putFloat(index + 6, klinear);
        buffer.putFloat(index + 7, kquadratic);
        ambient.get(index + 8, buffer);
        diffuse.get(index + 12, buffer);
        specular.get(index + 16, buffer);
        return buffer;
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
