package unknowndomain.engine.client;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.loader.AssetLoadManager;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.sound.ALSoundManager;
import unknowndomain.engine.util.Disposer;

public interface EngineClient extends Engine {

    Thread getRenderThread();

    default boolean isRenderThread() {
        return Thread.currentThread() == getRenderThread();
    }
    
    GameWindow getWindow();

    AssetLoadManager getAssetLoadManager();

    AssetManager getAssetManager();

    TextureManager getTextureManager();

    ALSoundManager getSoundManager();

    AssetSource getEngineAssetSource();

    Disposer getDisposer();

    @Override
    GameClient getCurrentGame();
}
