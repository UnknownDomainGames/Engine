package engine.client.sound;

import com.github.mouse0w0.observable.value.MutableValue;
import engine.client.asset.AssetURL;
import engine.graphics.camera.Camera;
import engine.util.Disposable;

public interface ALSoundManager extends Disposable {
    void init(String device) throws IllegalStateException;

    void updateListener(Camera camera);

    ALSoundSource createSoundSource(String name);

    MutableValue<ALSoundSource> getSoundSource(String name);

    MutableValue<ALSound> getSound(AssetURL url);

    ALSound getSoundDirect(AssetURL url);

    ALSoundListener getListener();
}
