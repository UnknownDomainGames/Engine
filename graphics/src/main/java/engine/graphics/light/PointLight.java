package engine.graphics.light;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class PointLight extends Light {
    private final Vector3f position = new Vector3f();

    private float kconstant = 1.0f;
    private float klinear;
    private float kquadratic;

    @Override
    public ByteBuffer get(MemoryStack stack) {
        return get(stack.malloc(40));
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        color.getRGB(index, buffer);
        buffer.putFloat(index + 12, intensity);
        position.get(index + 16, buffer);
        buffer.putFloat(index + 28, kconstant);
        buffer.putFloat(index + 32, klinear);
        buffer.putFloat(index + 36, kquadratic);
        return buffer;
    }

    @Override
    public void setup(Matrix4fc viewMatrix) {

    }

    public Vector3fc getPosition() {
        return position;
    }

    public PointLight setPosition(Vector3fc position) {
        this.position.set(position);
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
}
