package nullengine.block;

import nullengine.component.GameObject;
import nullengine.registry.Registrable;

import javax.annotation.Nonnull;

public interface Block extends Registrable<Block>, GameObject<Block> {
    // think about blockstate and tileentity...

    @Nonnull
    BlockShape getShape();
}
