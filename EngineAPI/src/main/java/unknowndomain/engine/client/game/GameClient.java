package unknowndomain.engine.client.game;

import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.input.controller.EntityController;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;

public interface GameClient extends Game {

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
