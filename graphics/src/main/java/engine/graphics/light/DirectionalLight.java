package engine.graphics.light;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class DirectionalLight extends Light {
    private Vector3f direction;

    @Override
    public ByteBuffer get(MemoryStack stack) {
        return get(stack.malloc(17 * Float.BYTES));
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        buffer.putInt(index, 1);
        direction.get(index + 1, buffer);
        ambient.get(index + 5, buffer);
        diffuse.get(index + 9, buffer);
        specular.get(index + 13, buffer);
        return buffer;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public DirectionalLight setDirection(Vector3f direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public DirectionalLight setAmbient(Vector3f ambient) {
        super.setAmbient(ambient);
        return this;
    }

    @Override
    public DirectionalLight setDiffuse(Vector3f diffuse) {
        super.setDiffuse(diffuse);
        return this;
    }

    @Override
    public DirectionalLight setSpecular(Vector3f specular) {
        super.setSpecular(specular);
        return this;
    }
}
