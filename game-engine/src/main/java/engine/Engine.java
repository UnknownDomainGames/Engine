package engine;

import engine.client.game.ClientGame;
import engine.event.EventBus;
import engine.game.Game;
import engine.mod.ModManager;
import engine.registry.RegistryManager;
import engine.util.CrashHandler;
import engine.util.RuntimeEnvironment;
import engine.util.Side;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * really, just the {@link Game} starter, nothing else
 */
public interface Engine {

    Path getRunPath();

    Logger getLogger();

    RuntimeEnvironment getRuntimeEnvironment();

    Side getSide();

    default boolean isClient() {
        return getSide() == Side.CLIENT;
    }

    default boolean isServer() {
        return getSide() == Side.SERVER;
    }

    EventBus getEventBus();

    RegistryManager getRegistryManager();

    ModManager getModManager();

    CrashHandler getCrashHandler();

    void start();

    void terminate();

    boolean isMarkedTermination();

    Game getGame();

    void runGame(Game game);

    ClientGame getClientGame();

    void runClientGame(ClientGame game);

    boolean isPlaying();

    void addShutdownListener(Runnable runnable);

    // TODO: client should add player profile manager here, to perform login,
    // logout, fetch skin and other operation
}
