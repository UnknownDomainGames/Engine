package engine.block;

import engine.block.state.BlockState;
import engine.component.GameObject;
import engine.registry.Registrable;
import engine.state.IncludeState;

import javax.annotation.Nonnull;

public interface Block extends Registrable<Block>, GameObject<Block>, IncludeState<Block, BlockState> {
    // think about blockstate and tileentity...

    @Nonnull
    BlockShape getShape();
}
