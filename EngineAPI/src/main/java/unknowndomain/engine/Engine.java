package unknowndomain.engine;

import org.slf4j.Logger;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.util.RuntimeEnvironment;
import unknowndomain.engine.util.Side;

/**
 * really, just the {@link Game} starter, nothing else
 */
public interface Engine {

    Logger getLogger();

    Side getSide();

    default boolean isClient() {
        return getSide() == Side.CLIENT;
    }

    default boolean isServer() {
        return getSide() == Side.SERVER;
    }

    RuntimeEnvironment getRuntimeEnvironment();

    void terminate();

    boolean isTerminated();

    void addShutdownListener(Runnable runnable);

    /**
     * Initialize the Engine. Load all mods and complete registration
     */
    void initEngine();

    void runEngine();

    EventBus getEventBus();

    void startGame(Game game);

    Game getCurrentGame();

    boolean isPlaying();

    // TODO: client should add player profile manager here, to perform login,
    // logout, fetch skin and other operation
}
