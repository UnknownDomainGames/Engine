package unknowndomain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.game.Game;

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
     * Start a new game, each engine only support one game at the time?
     *
     * @param option The game option
     */
    Game startGame(Game.Option option);

    // TODO: add getter for mod repository

    // TODO: add getter for resource repository

    Game getCurrentGame();

    // TODO: client should add player profile manager here, to perform login, logout, fetch skin and other operation

    // TODO: Remove it
    enum Side {
        SERVER, CLIENT
    }
}
