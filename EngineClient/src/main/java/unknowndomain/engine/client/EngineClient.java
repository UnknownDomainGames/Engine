package unknowndomain.engine.client;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.mod.*;
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

        store = new JavaModStore(Paths.get("mods")) { // for debug

            @Override
            public boolean exists(@NonNull ModIdentifier identifier) {
                if (ModIdentifier.of("", "minecraft", "").equals(identifier)) {
                    return true;
                }
                return super.exists(identifier);
            }

            @Override
            public ModContainer load(@NonNull ModIdentifier identifier) {
                if (ModIdentifier.of("", "minecraft", "").equals(identifier)) {
                    return new InnerModContainer(ModMetadata.Builder.create().setId("minecraft").build(), null, new MinecraftMod());
                }
                return super.load(identifier);
            }
        };
        repository = new ModRepositoryCollection();
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public Game startGame(Game.Option option) {
        game = new GameClientStandalone(new Game.Option(Lists.newArrayList(ModMetadata.Builder.create().setId("minecraft").build()), Collections.emptyList()),
                repository, store, bus, window);
        game.run();
        return game;
    }

    @Override
    public GameClientStandalone getCurrentGame() {
        return game;
    }
}
