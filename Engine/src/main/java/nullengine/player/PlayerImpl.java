package nullengine.player;

import nullengine.Platform;
import nullengine.entity.Entity;
import nullengine.event.player.PlayerControlEntityEvent;
import nullengine.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;

public class PlayerImpl implements Player {

    private final Profile profile;

    private Entity controlledEntity;

    public PlayerImpl(Profile profile) {
        this.profile = profile;
    }

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
