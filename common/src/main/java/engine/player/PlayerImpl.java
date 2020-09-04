package engine.player;

import engine.Platform;
import engine.entity.Entity;
import engine.event.player.PlayerControlEntityEvent;
import engine.server.network.NetworkHandler;
import engine.world.World;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

public class PlayerImpl implements Player {

    private final Profile profile;

    private Entity controlledEntity;

    private NetworkHandler handler;

    public PlayerImpl(Profile profile, Entity controlledEntity) {
        this.profile = profile;
        controlEntity(controlledEntity);
    }

    public PlayerImpl(Profile profile, NetworkHandler handler, Entity controlledEntity) {
        this.profile = profile;
        this.handler = handler;
        controlEntity(controlledEntity);
    }

//    public void enter(ChunkStorage chunkStore) {
//        int radius = this.profile.trackingChunkRadius;
//        Entity entity = this.getControlledEntity();
//        BlockPos pos = BlockPos.of(entity.getPosition());
//        for (int i = -radius; i <= radius; i++) {
//            if (i <= 0) {
//                int zOff = i + radius;
//                for (int j = -zOff; j <= zOff; j++)
//                    chunkStore.touchChunk(pos.add(i * 16, 0, j * 16));
//            } else {
//                int zOff = radius - i;
//                for (int j = -zOff; j <= zOff; j++)
//                    chunkStore.touchChunk(pos.add(i * 16, 0, j * 16));
//            }
//        }
//    }


    @Override
    public void tick() {

    }

    @Nonnull
    @Override
    public Entity getControlledEntity() {
        return controlledEntity;
    }

    @Nonnull
    @Override
    public Entity controlEntity(@Nonnull Entity entity) {
        Validate.notNull(entity);
        if (controlledEntity == entity) {
            return controlledEntity;
        }

        var old = controlledEntity;
        var event = new PlayerControlEntityEvent.Pre(this, old, entity);
        if (Platform.getEngine().getEventBus().post(event)) {
            return entity;
        }
        controlledEntity = event.getNewEntity();
        Platform.getEngine().getEventBus().post(new PlayerControlEntityEvent.Post(this, old, entity));
        return old;
    }

    @Override
    public boolean isControllingEntity() {
        return controlledEntity != null;
    }

    @Nonnull
    @Override
    public Profile getProfile() {
        return profile;
    }

    @Override
    public NetworkHandler getNetworkHandler() {
        return handler;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return controlledEntity.getWorld();
    }
}
