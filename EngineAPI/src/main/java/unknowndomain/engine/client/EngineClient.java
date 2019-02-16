package unknowndomain.engine.client;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.loader.AssetLoadManager;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.gui.GuiManager;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.sound.ALSoundManager;
import unknowndomain.engine.util.Disposer;

public interface EngineClient extends Engine {

    Thread getClientThread();

    boolean isClientThread();

    GameWindow getWindow();

    AssetManager getAssetManager();

    AssetLoadManager getAssetLoadManager();

    TextureManager getTextureManager();

    ALSoundManager getSoundManager();

    AssetSource getEngineAssetSource();

    GuiManager getGuiManager();

    Disposer getDisposer();

    @Override
    GameClient getCurrentGame();
}
