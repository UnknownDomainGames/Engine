package engine.client.game;

import engine.client.EngineClient;
import engine.client.player.ClientPlayer;
import engine.game.Game;
import engine.world.World;
import engine.world.hit.HitResult;

import javax.annotation.Nonnull;

public interface GameClient extends Game {

    @Nonnull
    @Override
    EngineClient getEngine();

    @Nonnull
    ClientPlayer getClientPlayer();

    @Nonnull
    World getClientWorld();

    HitResult getHitResult();

    void clientTick();
}
