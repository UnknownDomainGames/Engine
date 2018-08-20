package unknowndomain.engine.entity;

import org.joml.AABBd;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * The player in world instance, the lifecycle is binding to world
 * <p>
 * Track the chunk, other entity and send message to client/server
 */
public interface Player {
    @Nonnull
    Data getData();

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
    Entity getMountingEntity();

    /**
     * Mount a entity
     *
     * @param entity The entity will be mount, if it's null, the player will automatically spawn and mount default player entity
     * @return The old entity mounted
     */
    @Nonnull
    Entity mountEntity(@Nullable Entity entity);

    class Data {
        public final UUID uuid;
        public final int trackingChunkRadius;
        private AABBd boundingBox = new AABBd(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
        private String mountingEntity;

        public Data(UUID uuid, int trackingChunkRadius) {
            this.uuid = uuid;
            this.trackingChunkRadius = trackingChunkRadius;
        }

        public AABBd getBoundingBox() {
            return boundingBox;
        }

        public String getMountingEntity() {
            return mountingEntity;
        }
    }
}
