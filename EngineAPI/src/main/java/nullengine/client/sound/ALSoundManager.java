package nullengine.client.sound;

import com.github.mouse0w0.observable.value.MutableValue;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.camera.OldCamera;
import nullengine.util.Disposable;

public interface ALSoundManager extends Disposable {
    void init(String device) throws IllegalStateException;

    void updateListener(OldCamera camera);

    ALSoundSource createSoundSource(String name);

    MutableValue<ALSoundSource> getSoundSource(String name);

    MutableValue<ALSound> getSound(AssetURL url);

    ALSound getSoundDirect(AssetURL url);

    ALSoundListener getListener();
}
