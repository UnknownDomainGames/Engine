package engine.graphics.light;

import engine.util.Color;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;

public class DirectionalLight extends Light {
    private final Vector3f direction = new Vector3f();
    private final Vector3f viewDirection = new Vector3f();

    @Override
    public int sizeof() {
        return 28;
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        color.getRGB(index, buffer);
        buffer.putFloat(index + 12, intensity);
        viewDirection.get(index + 16, buffer);
        return buffer;
    }

    @Override
    public void update(Matrix4fc viewMatrix) {
        direction.mulDirection(viewMatrix, viewDirection);
    }

    @Override
    public DirectionalLight setColor(Color color) {
        super.setColor(color);
        return this;
    }

    @Override
    public DirectionalLight setIntensity(float intensity) {
        super.setIntensity(intensity);
        return this;
    }

    public Vector3fc getDirection() {
        return direction;
    }

    public DirectionalLight setDirection(Vector3fc direction) {
        this.direction.set(direction).normalize();
        return this;
    }

    public DirectionalLight setDirection(float x, float y, float z) {
        direction.set(x, y, z).normalize();
        return this;
    }

    public Vector3fc getViewDirection() {
        return viewDirection;
    }
}
