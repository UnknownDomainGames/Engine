package unknowndomain.engine.player;

import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The player in world instance, the lifecycle is binding to world
 * <p>
 * Track the chunk, other entity and send message to client/server
 */
public interface Player {
    @Nonnull
    Profile getProfile();

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
    Entity controlEntity(@Nullable Entity entity);
}
