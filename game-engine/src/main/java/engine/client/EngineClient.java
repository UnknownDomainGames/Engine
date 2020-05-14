package engine.client;

import engine.Engine;
import engine.client.asset.AssetManager;
import engine.client.game.ClientGame;
import engine.client.settings.EngineSettings;
import engine.client.sound.SoundManager;
import engine.graphics.GraphicsManager;

public interface EngineClient extends Engine {

    Thread getClientThread();

    boolean isClientThread();

    AssetManager getAssetManager();

    GraphicsManager getGraphicsManager();

    SoundManager getSoundManager();

    @Override
    ClientGame getClientGame();

    EngineSettings getSettings();
}
