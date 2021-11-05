package engine.registry.game;

import engine.block.Block;
import engine.block.state.BlockState;
import engine.registry.impl.SynchronizableStateIdRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class BlockRegistryImpl extends SynchronizableStateIdRegistry<Block, BlockState> implements BlockRegistry {

    private Block air;

    public BlockRegistryImpl() {
        super(Block.class, BlockState.class);
    }

    @Nonnull
    @Override
    public Block air() {
        return air;
    }

    @Override
    public void setAirBlock(@Nonnull Block air) {
        if (this.air != null)
            throw new IllegalStateException("Block air has been set");

        this.air = Objects.requireNonNull(air);
    }

    @Override
    public String serializeState(BlockState state) {
        return state.toStorageString();
    }

    @Override
    public BlockState deserializeState(String state) {
        return getValue(BlockState.getBlockNameFromStorageString(state)).getStateManager().getDefaultState().fromStorageString(state);
    }

}
