package unknowndomain.engine.client;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.sound.ALSoundManager;
import unknowndomain.engine.util.disposer.Disposer;

public interface EngineClient extends Engine {

    Thread getClientThread();

    boolean isClientThread();

    AssetManager getAssetManager();

    AssetSource getEngineAssetSource();

    RenderContext getRenderContext();

    ALSoundManager getSoundManager();

    Disposer getDisposer();

    @Override
    GameClient getCurrentGame();
}
