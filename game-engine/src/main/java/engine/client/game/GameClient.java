package engine.client.game;

import engine.client.EngineClient;
import engine.client.player.ClientPlayer;
import engine.game.Game;
import engine.world.World;

import javax.annotation.Nonnull;

public interface GameClient extends Game {

    @Nonnull
    @Override
    EngineClient getEngine();

    @Nonnull
    ClientPlayer getClientPlayer();

    @Nonnull
    World getClientWorld();

    void clientTick();
}
