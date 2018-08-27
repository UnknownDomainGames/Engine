package unknowndomain.engine.client;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

import com.google.common.collect.Lists;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModRepositoryCollection;
import unknowndomain.engine.mod.ModStore;
import unknowndomain.engine.mod.ModStoreLocal;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private DefaultGameWindow window;

    private EventBus bus;

    private ModStore store;
    private ModRepositoryCollection repository;
    private Player.Profile playerProfile;
    private GameClientStandalone game;

    EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());
    }

    void start() {
        window.init();

        bus = new AsmEventBus();
        store = new ModStoreLocal(Paths.get("mods"));
        repository = new ModRepositoryCollection();
        playerProfile = new Player.Profile(UUID.randomUUID(), 12);
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public Game startGame(Game.Option option) {
        // prepare
        game = new GameClientStandalone(new Game.Option(Lists.newArrayList(), Lists.newArrayList()), repository, store,
                bus, window);
        game.run();

        // stop last game
        return game;
    }

    @Override
    public GameClientStandalone getCurrentGame() {
        return game;
    }
}
