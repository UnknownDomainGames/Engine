package unknowndomain.engine.item;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ItemBase extends RegistryEntry.Impl<Item> implements Item {
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
    public <T extends Component> void setComponent(@Nonnull Class<T> type, T value) {

    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {

    }

}
