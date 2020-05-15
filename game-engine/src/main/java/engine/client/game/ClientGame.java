package engine.client.game;

import engine.client.ClientEngine;
import engine.client.player.ClientPlayer;
import engine.game.Game;
import engine.world.World;

import javax.annotation.Nonnull;

public interface ClientGame extends Game {

    @Nonnull
    @Override
    ClientEngine getEngine();

    @Nonnull
    ClientPlayer getClientPlayer();

    @Nonnull
    World getClientWorld();

    void clientTick();
}
