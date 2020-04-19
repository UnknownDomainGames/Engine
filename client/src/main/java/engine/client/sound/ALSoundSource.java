package engine.client.sound;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryStack.*;

public final class ALSoundSource implements SoundSource {

    private int id = 0;
    private boolean loop;
    private boolean relative;

    public ALSoundSource(boolean loop, boolean relative) {
        this.loop = loop;
        this.relative = relative;
    }

    @Override
    public void createSource() {
        if (id == 0) {
            id = alGenSources();
            alSourcei(id, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
            alSourcei(id, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        }
    }

    @Override
    public void assign(Sound sound) {
        stop();
        alSourcei(id, AL_BUFFER, ((ALSound) sound).getId());
    }

    @Override
    public void stop() {
        alSourceStop(id);
    }

    @Override
    public void pause() {
        alSourcePause(id);
    }

    @Override
    public void play() {
        alSourcePlay(id);
    }

    @Override
    public boolean isPlaying() {
        return alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return alGetSourcei(id, AL_SOURCE_STATE) == AL_PAUSED;
    }

    @Override
    public boolean isStopped() {
        return alGetSourcei(id, AL_SOURCE_STATE) == AL_STOPPED;
    }

    public int getId() {
        return id;
    }

    @Override
    public SoundSource position(float x, float y, float z) {
        alSource3f(id, AL_POSITION, x, y, z);
        return this;
    }

    @Override
    public Vector3f getPosition() {
        stackPush();
        FloatBuffer a = stackMallocFloat(1);
        FloatBuffer a1 = stackMallocFloat(1);
        FloatBuffer a2 = stackMallocFloat(1);
        alGetSource3f(id, AL_POSITION, a, a1, a2);
        Vector3f v = new Vector3f(a.get(), a1.get(), a2.get());
        stackPop();
        return v;
    }

    @Override
    public SoundSource position(Vector3fc pos) {
        return position(pos.x(), pos.y(), pos.z());
    }

    @Override
    public SoundSource speed(float x, float y, float z) {
        alSource3f(id, AL_VELOCITY, x, y, z);
        return this;
    }

    @Override
    public SoundSource speed(Vector3fc speed) {
        return speed(speed.x(), speed.y(), speed.z());
    }

    @Override
    public Vector3f getSpeed() {
        stackPush();
        FloatBuffer a = stackMallocFloat(1);
        FloatBuffer a1 = stackMallocFloat(1);
        FloatBuffer a2 = stackMallocFloat(1);
        alGetSource3f(id, AL_VELOCITY, a, a1, a2);
        Vector3f v = new Vector3f(a.get(), a1.get(), a2.get());
        stackPop();
        return v;
    }

    @Override
    public SoundSource gain(float gain) {
        alSourcef(id, AL_GAIN, gain);
        return this;
    }

    @Override
    public SoundSource setLoop(boolean flag) {
        loop = flag;
        alSourcei(id, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        return this;
    }

    @Override
    public SoundSource setRelative(boolean relative) {
        this.relative = relative;
        alSourcei(id, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        return this;
    }

    @Override
    public void dispose() {
        if (id != 0) {
            stop();
            alDeleteSources(id);
            id = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }
}
