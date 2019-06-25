package nullengine.client.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;


/**
 * Sound listener wrapper of OpenAL.
 */
public class ALSoundListener {


    public ALSoundListener() {
        this(new Vector3f(0));
    }

    public ALSoundListener(Vector3f pos) {
        alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }

    public ALSoundListener speed(float x, float y, float z) {
        alListener3f(AL_VELOCITY, x, y, z);
        return this;
    }

    public ALSoundListener speed(Vector3f speed) {
        return speed(speed.x, speed.y, speed.z);
    }

    public ALSoundListener position(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
        return this;
    }

    public ALSoundListener position(Vector3f pos) {
        return position(pos.x, pos.y, pos.z);
    }

    public ALSoundListener orient(Vector3f target, Vector3f up) {
        float[] data = new float[]{target.x, target.y, target.z, up.x, up.y, up.z};
        alListenerfv(AL_ORIENTATION, data);
        return this;
    }

    public ALSoundListener orient(Vector3f target) {
        return orient(target, new Vector3f(0, 1, 0));
    }

    public ALSoundListener orient(float x, float y, float z) {
        return orient(new Vector3f(x, y, z));
    }

}
