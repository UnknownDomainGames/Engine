package nullengine.client;

import nullengine.Engine;
import nullengine.client.asset.AssetManager;
import nullengine.client.asset.source.AssetSource;
import nullengine.client.game.GameClient;
import nullengine.client.rendering.RenderManager;
import nullengine.client.sound.ALSoundManager;
import nullengine.util.disposer.Disposer;

public interface EngineClient extends Engine {

    Thread getClientThread();

    boolean isClientThread();

    AssetManager getAssetManager();

    AssetSource getEngineAssetSource();

    RenderManager getRenderManager();

    ALSoundManager getSoundManager();

    Disposer getDisposer();

    @Override
    GameClient getCurrentGame();
}
