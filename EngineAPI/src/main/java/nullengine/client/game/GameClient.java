package nullengine.client.game;

import nullengine.client.EngineClient;
import nullengine.client.input.controller.EntityController;
import nullengine.game.Game;
import nullengine.player.Player;
import nullengine.world.World;

import javax.annotation.Nonnull;

public interface GameClient extends Game {

    @Nonnull
    @Override
    EngineClient getEngine();

    @Nonnull
    Player getPlayer();

    @Nonnull
    World getWorld();

    EntityController getEntityController();

    void setEntityController(EntityController controller);

    void clientTick();
}
