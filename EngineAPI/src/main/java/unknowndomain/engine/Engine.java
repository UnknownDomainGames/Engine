package unknowndomain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.util.Side;

/**
 * really, just the {@link Game} starter, nothing else
 */
public interface Engine {
    Logger LOGGER = LoggerFactory.getLogger("Engine");

    @Deprecated
    static Logger getLogger() {
        return LOGGER;
    }

    Side getSide();

    default boolean isClient() {
        return getSide() == Side.CLIENT;
    }

    default boolean isServer() {
        return getSide() == Side.SERVER;
    }

    /**
     * Initialize the Engine. Load all mods and complete registration
     */
    void initEngine();

    EventBus getEventBus();

    RegistryManager getRegistryManager();

    /**
     * Start a new game, each engine only support one game at the time?
     */
    void startGame();

    // TODO: add getter for mod repository

    // TODO: add getter for resource repository

    Game getCurrentGame();

    // TODO: client should add player profile manager here, to perform login,
    // logout, fetch skin and other operation
}
