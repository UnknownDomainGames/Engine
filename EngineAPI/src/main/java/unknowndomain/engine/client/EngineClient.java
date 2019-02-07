package unknowndomain.engine.client;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.asset.AssetLoadManager;
import unknowndomain.engine.client.asset.source.AssetManager;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.sound.ALSoundManager;

public interface EngineClient extends Engine {

    GameWindow getWindow();

    AssetLoadManager getAssetLoadManager();

    AssetManager getAssetManager();

    TextureManager getTextureManager();

    ALSoundManager getSoundManager();

    AssetSource getEngineAssetSource();

    @Override
    GameClient getCurrentGame();
}
