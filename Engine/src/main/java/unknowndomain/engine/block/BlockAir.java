package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

@Deprecated
public class BlockAir extends RegistryEntry.Impl<Block> implements Block {

    public BlockAir() {
        localName("air");
    }

    @Override
    public AABBd[] getBoundingBoxes() {
        return new AABBd[0];
    }

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return Optional.empty();
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
        return Set.of();
    }
}
