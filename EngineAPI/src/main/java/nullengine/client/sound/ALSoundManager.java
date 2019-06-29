package nullengine.client.sound;

import com.github.mouse0w0.observable.value.MutableValue;
import nullengine.client.asset.AssetPath;
import nullengine.client.rendering.camera.Camera;
import nullengine.util.disposer.Disposable;

public interface ALSoundManager extends Disposable {
    void init(String device) throws IllegalStateException;

    void updateListener(Camera camera);

    ALSoundSource createSoundSource(String name);

    MutableValue<ALSoundSource> getSoundSource(String name);

    MutableValue<ALSound> getSound(AssetPath path);

    ALSound getSoundDirect(AssetPath path);

    ALSoundListener getListener();
}
