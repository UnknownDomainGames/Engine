package engine.client;

import engine.Engine;
import engine.client.asset.AssetManager;
import engine.client.settings.EngineSettings;
import engine.client.sound.SoundManager;
import engine.graphics.GraphicsManager;

public interface ClientEngine extends Engine {

    AssetManager getAssetManager();

    GraphicsManager getGraphicsManager();

    SoundManager getSoundManager();

    EngineSettings getSettings();
}
