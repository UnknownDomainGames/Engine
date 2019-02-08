package unknowndomain.engine.client.sound;

import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.util.Disposable;

public interface ALSoundManager extends Disposable {
    void init(String device) throws IllegalStateException;

    void updateListener(Camera camera);

    ALSoundSource createSoundSource(String name);

    ALSoundSource getSoundSource(String name);

    ALSound getSound(AssetPath path, boolean reload);

    default ALSound getSound(AssetPath path){
        return getSound(path, false);
    }

    ALSoundListener getListener();
}
