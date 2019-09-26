package nullengine.block;

import nullengine.component.GameObject;
import nullengine.registry.RegistryEntry;

import javax.annotation.Nonnull;

public interface Block extends RegistryEntry<Block>, GameObject<Block> {
    // think about blockstate and tileentity...

    @Nonnull
    BlockShape getShape();
}
