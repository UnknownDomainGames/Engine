package engine.player;

import engine.entity.Entity;
import engine.server.network.NetworkHandler;
import engine.world.World;

import javax.annotation.Nonnull;

/**
 * The player in world instance, the lifecycle is binding to world
 * <p>
 * Track the chunk, other entity and send message to client/server
 */
public interface Player {

    @Nonnull
    Profile getProfile();

    NetworkHandler getNetworkHandler();

    /**
     * @return the binding world of this player
     */
    @Nonnull
    World getWorld();

    /**
     * The current mounting entity of this player, even there is a setter for this,
     * this method should not return null
     *
     * @return The mounting entity
     */
    @Nonnull
    Entity getControlledEntity();

    /**
     * Mount a entity
     *
     * @param entity The entity will be mount, if it's null, the player will automatically spawn and mount default player entity
     * @return The old entity mounted
     */
    @Nonnull
    Entity controlEntity(@Nonnull Entity entity);

    boolean isControllingEntity();

    void tick();
}
