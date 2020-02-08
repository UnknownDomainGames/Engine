package engine.block;

import engine.component.GameObject;
import engine.registry.Registrable;

import javax.annotation.Nonnull;

public interface Block extends Registrable<Block>, GameObject<Block> {
    // think about blockstate and tileentity...

    @Nonnull
    BlockShape getShape();
}
