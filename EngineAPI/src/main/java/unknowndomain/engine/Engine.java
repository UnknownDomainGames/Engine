package unknowndomain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.game.Game;

/**
 * really, just the game starter, nothing else
 */
public interface Engine {
    Logger LOGGER = LoggerFactory.getLogger("Engine");

    static Logger getLogger() {
        return LOGGER;
    }

    Side getSide();

    /**
     * Start a new game, each engine only support one game at the time?
     *
     * @param config The game config
     */
    Game startGame(Game.Config config);

    // TODO: add getter for mod repository

    // TODO: add getter for resource repository

    Game getCurrentGame();

    enum Side {
        SERVER, CLIENT
    }
}
