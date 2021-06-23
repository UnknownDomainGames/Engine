package engine.client;

import engine.Engine;
import engine.client.asset.AssetManager;
import engine.client.settings.EngineSettings;
import engine.client.sound.SoundManager;
import engine.graphics.GraphicsManager;
import engine.player.Profile;

public interface EngineClient extends Engine {

    Thread getClientThread();

    boolean isClientThread();

    AssetManager getAssetManager();

    GraphicsManager getGraphicsManager();

    SoundManager getSoundManager();

    Profile getPlayerProfile();

    EngineSettings getSettings();

    boolean isGamePaused();

    void setGamePauseState(boolean paused);
}
