package unknowndomain.engine.client;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.mod.ModRepositoryCollection;
import unknowndomain.engine.mod.ModStore;
import unknowndomain.engine.mod.java.JavaModStore;

import java.nio.file.Paths;
import java.util.Collections;

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

        store = new JavaModStore(Paths.get("mods"));
        repository = new ModRepositoryCollection();
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public Game startGame(Game.Option option) {

        game = new GameClientStandalone(new Game.Option(Collections.emptyList(), Collections.emptyList()),
                repository, store, bus, window);

        MinecraftMod mod = new MinecraftMod();
        bus.register(mod);

        game.run();
        return game;
    }

    @Override
    public GameClientStandalone getCurrentGame() {
        return game;
    }
}
