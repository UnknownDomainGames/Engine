package unknowndomain.engine;

import org.slf4j.Logger;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.util.RuntimeEnvironment;
import unknowndomain.engine.util.Side;

/**
 * really, just the {@link Game} starter, nothing else
 */
public interface Engine {

    Logger getLogger();

    RuntimeEnvironment getRuntimeEnvironment();

    Side getSide();

    default boolean isClient() {
        return getSide() == Side.CLIENT;
    }

    default boolean isServer() {
        return getSide() == Side.DEDICATED_SERVER;
    }

    EventBus getEventBus();

    ModManager getModManager();

    /**
     * Initialize the Engine. Load all mods and complete registration
     */
    void initEngine();

    void runEngine();

    Game getCurrentGame();

    void startGame(Game game);

    boolean isPlaying();

    void terminate();

    boolean isMarkedTermination();

    void addShutdownListener(Runnable runnable);

    // TODO: client should add player profile manager here, to perform login,
    // logout, fetch skin and other operation
}
