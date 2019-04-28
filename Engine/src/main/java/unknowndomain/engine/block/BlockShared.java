package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

@Deprecated
class BlockShared extends RegistryEntry.Impl<Block> implements Block {
    private AABBd[] boundingBox;

    BlockShared(AABBd[] boundingBox, BlockPrototype.PlaceBehavior placeBehavior, BlockPrototype.ActivateBehavior activateBehavior, BlockPrototype.ClickBehavior clickBehavior, BlockPrototype.DestroyBehavior destroyBehavior) {
        this.boundingBox = boundingBox;

        setComponent(BlockPrototype.PlaceBehavior.class, placeBehavior);
        setComponent(BlockPrototype.ActivateBehavior.class, activateBehavior);
        setComponent(BlockPrototype.ClickBehavior.class, clickBehavior);
        setComponent(BlockPrototype.DestroyBehavior.class, destroyBehavior);
    }

    @Override
    public AABBd[] getBoundingBoxes() {
        return boundingBox;
    }

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {

    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {

    }

    @Nonnull
    @Override
    public Set<Class<?>> getComponents() {
        return null;
    }
}
