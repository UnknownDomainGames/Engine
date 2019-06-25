package nullengine.math;

import org.joml.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Rotation extends Vector3f {

    public Rotation() {
        this(0, 0, 0);
    }

    public Rotation(float d) {
        super(d);
    }

    public Rotation(Vector3fc v) {
        super(v);
    }

    public Rotation(Vector3ic v) {
        super(v);
    }

    public Rotation(Vector2fc v, float z) {
        super(v, z);
    }

    public Rotation(Vector2ic v, float z) {
        super(v, z);
    }

    public Rotation(ByteBuffer buffer) {
        super(buffer);
    }

    public Rotation(int index, ByteBuffer buffer) {
        super(index, buffer);
    }

    public Rotation(FloatBuffer buffer) {
        super(buffer);
    }

    public Rotation(int index, FloatBuffer buffer) {
        super(index, buffer);
    }

    public Rotation(float pitch, float yaw, float roll) {
        super(pitch, yaw, roll);
    }

    public float getPitch() {
        return x;
    }

    public void setPitch(float pitch) {
        this.x = pitch;
    }

    public float getYaw() {
        return y;
    }

    public void setYaw(float yaw) {
        this.y = yaw;
    }

    public float getRoll() {
        return z;
    }

    public void setRoll(float roll) {
        this.z = roll;
    }

    @Override
    public String toString() {
        return "Rotation{" +
                "pitch=" + x +
                ", yaw=" + y +
                ", roll=" + z +
                '}';
    }
}
