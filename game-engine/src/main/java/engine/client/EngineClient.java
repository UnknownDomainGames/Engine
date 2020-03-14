package engine.client;

import engine.Engine;
import engine.client.asset.AssetManager;
import engine.client.asset.source.AssetSource;
import engine.client.game.GameClient;
import engine.client.settings.EngineSettings;
import engine.client.sound.ALSoundManager;
import engine.graphics.GraphicsManager;

public interface EngineClient extends Engine {

    Thread getClientThread();

    boolean isClientThread();

    AssetManager getAssetManager();

    AssetSource getEngineAssetSource();

    GraphicsManager getGraphicsManager();

    ALSoundManager getSoundManager();

    @Override
    GameClient getCurrentGame();

    EngineSettings getSettings();
}
