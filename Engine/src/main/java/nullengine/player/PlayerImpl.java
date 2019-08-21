package nullengine.player;

import nullengine.Platform;
import nullengine.entity.Entity;
import nullengine.server.network.NetworkHandler;
import nullengine.event.player.PlayerControlEntityEvent;
import nullengine.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;

public class PlayerImpl implements Player {

    private final Profile profile;

    private Entity controlledEntity;

    private NetworkHandler handler;

    public PlayerImpl(Profile profile) {
        this.profile = profile;
    }

    public PlayerImpl(Profile profile, NetworkHandler handler){
        this.profile = profile;
        this.handler = handler;
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

    @NonNull
    @Override
    public Entity getControlledEntity() {
        return controlledEntity;
    }

    @NonNull
    @Override
    public Entity controlEntity(@Nonnull Entity entity) {
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

    @NonNull
    @Override
    public World getWorld() {
        return controlledEntity.getWorld();
    }
}
