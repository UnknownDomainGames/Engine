package engine.graphics.light;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;

public class SpotLight extends Light {
    private final Vector3f position = new Vector3f();
    private final Vector3f viewPosition = new Vector3f();

    private float kconstant = 1.0f;
    private float klinear;
    private float kquadratic;

    private final Vector3f direction = new Vector3f();
    private final Vector3f viewDirection = new Vector3f();
    private float cutoffAngle; // in Radian
    private float outerCutoffAngle; // in Radian

    @Override
    public int sizeof() {
        return 60;
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        color.getRGB(index, buffer);
        buffer.putFloat(index + 12, intensity);
        viewPosition.get(index + 16, buffer);
        buffer.putFloat(index + 28, kconstant);
        buffer.putFloat(index + 32, klinear);
        buffer.putFloat(index + 36, kquadratic);
        viewDirection.get(index + 40, buffer);
        buffer.putFloat(index + 52, (float) Math.cos(cutoffAngle));
        buffer.putFloat(index + 56, (float) Math.cos(outerCutoffAngle));
        return buffer;
    }

    @Override
    public void update(Matrix4fc viewMatrix) {
        position.mulPosition(viewMatrix, viewPosition);
        direction.mulDirection(viewMatrix, viewDirection);
    }

    public Vector3fc getPosition() {
        return position;
    }

    public SpotLight setPosition(Vector3fc position) {
        this.position.set(position);
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

    public Vector3fc getDirection() {
        return direction;
    }

    public SpotLight setDirection(Vector3fc direction) {
        this.direction.set(direction);
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
}
