package engine.client.sound;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface SoundSource {
    void createSource();

    void assign(Sound sound);

    void stop();

    void pause();

    void play();

    boolean isPlaying();

    boolean isPaused();

    boolean isStopped();

    SoundSource position(float x, float y, float z);

    Vector3f getPosition();

    SoundSource position(Vector3fc pos);

    SoundSource speed(float x, float y, float z);

    SoundSource speed(Vector3fc speed);

    Vector3f getSpeed();

    SoundSource gain(float gain);

    SoundSource setLoop(boolean flag);

    SoundSource setRelative(boolean relative);

    void dispose();

    boolean isDisposed();
}
