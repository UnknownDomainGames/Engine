package nullengine.client.sound;

import nullengine.util.disposer.Disposable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryStack.*;

public class ALSoundSource implements Disposable {

    private int sourceId = 0;
    private boolean loop;
    private boolean relative;

    public ALSoundSource(boolean loop, boolean relative) {
        this.loop = loop;
        this.relative = relative;
    }

    public void createSource() {
        if (sourceId == 0) {
            sourceId = alGenSources();
            alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
            alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        }
    }

    public void assignSound(ALSound sound) {
        stop();
        alSourcei(sourceId, AL_BUFFER, sound.getSoundId());
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void play() {
        alSourcePlay(sourceId);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public boolean isPaused() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED;
    }

    public boolean isStopped() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_STOPPED;
    }

    public int getSourceId() {
        return sourceId;
    }

    public ALSoundSource position(float x, float y, float z) {
        alSource3f(sourceId, AL_POSITION, x, y, z);
        return this;
    }

    public Vector3f getPosition() {
        stackPush();
        FloatBuffer a = stackMallocFloat(1);
        FloatBuffer a1 = stackMallocFloat(1);
        FloatBuffer a2 = stackMallocFloat(1);
        alGetSource3f(sourceId, AL_POSITION, a, a1, a2);
        Vector3f v = new Vector3f(a.get(), a1.get(), a2.get());
        stackPop();
        return v;
    }

    public ALSoundSource position(Vector3fc pos) {
        return position(pos.x(), pos.y(), pos.z());
    }

    public ALSoundSource speed(float x, float y, float z) {
        alSource3f(sourceId, AL_VELOCITY, x, y, z);
        return this;
    }

    public ALSoundSource speed(Vector3fc speed) {
        return speed(speed.x(), speed.y(), speed.z());
    }

    public Vector3f getSpeed() {
        stackPush();
        FloatBuffer a = stackMallocFloat(1);
        FloatBuffer a1 = stackMallocFloat(1);
        FloatBuffer a2 = stackMallocFloat(1);
        alGetSource3f(sourceId, AL_VELOCITY, a, a1, a2);
        Vector3f v = new Vector3f(a.get(), a1.get(), a2.get());
        stackPop();
        return v;
    }

    public ALSoundSource gain(float gain) {
        alSourcef(sourceId, AL_GAIN, gain);
        return this;
    }

    public ALSoundSource setLoop(boolean flag) {
        loop = flag;
        alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        return this;
    }

    public ALSoundSource setRelative(boolean relative) {
        this.relative = relative;
        alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        return this;
    }

    public void dispose() {
        if (sourceId != 0) {
            stop();
            alDeleteSources(sourceId);
            sourceId = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return sourceId == 0;
    }
}
