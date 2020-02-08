package engine.math;

import org.joml.Math;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public abstract class Vector3iBase implements Vector3ic {

    public IntBuffer get(IntBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    public IntBuffer get(int index, IntBuffer buffer) {
        buffer.position(index);
        buffer.put(x());
        buffer.put(y());
        buffer.put(z());
        return buffer;
    }

    public ByteBuffer get(ByteBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        buffer.position(index);
        buffer.putInt(x());
        buffer.putInt(y());
        buffer.putInt(z());
        return buffer;
    }

    public Vector3ic getToAddress(long address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector3i sub(Vector3ic v, Vector3i dest) {
        return sub(v.x(), v.y(), v.z(), dest);
    }

    @Override
    public Vector3i sub(int x, int y, int z, Vector3i dest) {
        dest.x = this.x() - x;
        dest.y = this.y() - y;
        dest.z = this.z() - z;
        return dest;
    }

    @Override
    public Vector3i add(Vector3ic v, Vector3i dest) {
        return add(v.x(), v.y(), v.z(), dest);
    }

    public Vector3i add(int x, int y, int z, Vector3i dest) {
        dest.x = this.x() + x;
        dest.y = this.y() + y;
        dest.z = this.z() + z;
        return dest;
    }

    @Override
    public Vector3i mul(int scalar, Vector3i dest) {
        return mul(scalar, scalar, scalar, dest);
    }

    public Vector3i mul(Vector3ic v, Vector3i dest) {
        return mul(v.x(), v.y(), v.z(), dest);
    }

    public Vector3i mul(int x, int y, int z, Vector3i dest) {
        dest.x = this.x() * x;
        dest.y = this.y() * y;
        dest.z = this.z() * z;
        return dest;
    }

    public long lengthSquared() {
        return Vector3i.lengthSquared(x(), y(), z());
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double distance(Vector3ic v) {
        return distance(v.x(), v.y(), v.z());
    }

    public double distance(int x, int y, int z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    public long gridDistance(Vector3ic v) {
        return Math.abs(v.x() - x()) + Math.abs(v.y() - y()) + Math.abs(v.z() - z());
    }

    public long gridDistance(int x, int y, int z) {
        return Math.abs(x - x()) + Math.abs(y - y()) + Math.abs(z - z());
    }

    @Override
    public long distanceSquared(Vector3ic v) {
        return distanceSquared(v.x(), v.y(), v.z());
    }

    @Override
    public long distanceSquared(int x, int y, int z) {
        int dx = this.x() - x;
        int dy = this.y() - y;
        int dz = this.z() - z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public Vector3i negate(Vector3i dest) {
        dest.x = -x();
        dest.y = -y();
        dest.z = -z();
        return dest;
    }

    @Override
    public Vector3i min(Vector3ic v, Vector3i dest) {
        dest.x = Math.min(x(), v.x());
        dest.y = Math.min(y(), v.y());
        dest.z = Math.min(z(), v.z());
        return dest;
    }

    @Override
    public Vector3i max(Vector3ic v, Vector3i dest) {
        dest.x = Math.max(x(), v.x());
        dest.y = Math.max(y(), v.y());
        dest.z = Math.max(z(), v.z());
        return dest;
    }

    @Override
    public int get(int component) throws IllegalArgumentException {
        switch (component) {
            case 0:
                return x();
            case 1:
                return y();
            case 2:
                return z();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int maxComponent() {
        return 3;
    }

    @Override
    public int minComponent() {
        return 3;
    }

    @Override
    public boolean equals(int x, int y, int z) {
        if (this.x() != x)
            return false;
        if (this.y() != y)
            return false;
        if (this.z() != z)
            return false;
        return true;
    }
}
