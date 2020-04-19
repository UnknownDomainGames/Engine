package engine.client.sound;

import com.github.mouse0w0.observable.value.MutableObjectValue;

public interface SoundManager {
    SoundSource createSoundSource(String name);

    MutableObjectValue<SoundSource> getSoundSource(String name);

    SoundListener getListener();
}
