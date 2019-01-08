package unknowndomain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.registry.RegistryManager;

/**
 * really, just the {@link Game} starter, nothing else
 */
public interface Engine {
    Logger LOGGER = LoggerFactory.getLogger("Engine");

    static Logger getLogger() {
        return LOGGER;
    }

    Side getSide();

    /**
     * Initialize the Engine. Load all mods and complete registration
     */
    void initEngine();

    EventBus getEventBus();

    RegistryManager getRegistryManager();

    /**
     * Start a new game, each engine only support one game at the time?
     *
     * @param option The game option
     */
    Game startGame(Game.Option option);

    // TODO: add getter for mod repository

    // TODO: add getter for resource repository

    Game getCurrentGame();

    // TODO: client should add player profile manager here, to perform login,
    // logout, fetch skin and other operation

    // TODO: Remove it
    enum Side {
        SERVER, CLIENT
    }
}
