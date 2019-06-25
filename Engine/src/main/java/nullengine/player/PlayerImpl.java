package nullengine.player;

import nullengine.entity.Entity;
import nullengine.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlayerImpl implements Player {

    private final Profile profile;

    private Entity controlledEntity;

    public PlayerImpl(Profile data) {
        this.profile = data;
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
    public Entity controlEntity(Entity entity) {
        Entity old = controlledEntity;
        if (entity == null) {
            // parse phantom entity if is god, else parse default player
        }
        controlledEntity = entity;
        // fire event;
        return old;
    }

    @Override
    public boolean isControllingEntity() {
        return controlledEntity != null;
    }

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
