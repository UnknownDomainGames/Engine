package nullengine.client.game;

import nullengine.client.EngineClient;
import nullengine.client.player.ClientPlayer;
import nullengine.game.Game;
import nullengine.world.World;
import nullengine.world.hit.HitResult;

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
