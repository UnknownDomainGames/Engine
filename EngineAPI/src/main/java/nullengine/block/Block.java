package nullengine.block;

import nullengine.component.Component;
import nullengine.component.GameObject;
import nullengine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Block extends RegistryEntry<Block>, GameObject {
    // think about blockstate and tileentity...

    @Nonnull
    BlockShape getShape();

    <T extends Component> Block addComponent(@Nonnull Class<T> type, @Nullable T value);
}
