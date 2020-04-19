package engine.client.sound;

import engine.graphics.camera.Camera;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;

/**
 * Sound listener wrapper of OpenAL.
 */
public final class ALSoundListener implements SoundListener {

    public ALSoundListener() {
        this(new Vector3f(0));
    }

    public ALSoundListener(Vector3f pos) {
        alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }

    @Override
    public SoundListener camera(Camera camera) {
        Matrix4fc viewMatrix = camera.getViewMatrix();
        // Optimized version to get lookAt vector and Up vector from View Matrix, provided by author of JOML at a LWJGL forum
        Vector3f at = new Vector3f();
        viewMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        viewMatrix.positiveY(up);
        return position(camera.getPosition()).orient(at, up);
    }

    @Override
    public SoundListener speed(float x, float y, float z) {
        alListener3f(AL_VELOCITY, x, y, z);
        return this;
    }

    @Override
    public SoundListener speed(Vector3fc speed) {
        return speed(speed.x(), speed.y(), speed.z());
    }

    @Override
    public SoundListener position(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
        return this;
    }

    @Override
    public SoundListener position(Vector3fc pos) {
        return position(pos.x(), pos.y(), pos.z());
    }

    @Override
    public SoundListener orient(Vector3fc target, Vector3fc up) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer data = stack.mallocFloat(6);
            data.put(target.x()).put(target.y()).put(target.z());
            data.put(up.x()).put(up.y()).put(up.z());
            data.flip();
            alListenerfv(AL_ORIENTATION, data);
        }
        return this;
    }

    @Override
    public SoundListener orient(Vector3fc target) {
        return orient(target, new Vector3f(0, 1, 0));
    }

    @Override
    public SoundListener orient(float x, float y, float z) {
        return orient(new Vector3f(x, y, z));
    }
}
