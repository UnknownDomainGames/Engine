package unknowndomain.engine.client.sound;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.util.disposer.Disposable;

public interface ALSoundManager extends Disposable {
    void init(String device) throws IllegalStateException;

    void updateListener(Camera camera);

    ALSoundSource createSoundSource(String name);

    MutableValue<ALSoundSource> getSoundSource(String name);

    MutableValue<ALSound> getSound(AssetPath path);

    ALSound getSoundDirect(AssetPath path);

    ALSoundListener getListener();
}
