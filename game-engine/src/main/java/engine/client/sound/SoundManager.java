package engine.client.sound;

public interface SoundManager {
    SoundSource createSoundSource(String name);

    SoundSource getSoundSource(String name);

    SoundListener getListener();
}
