package unknowndomain.game.registry;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.registry.game.BlockRegistry;
import unknowndomain.engine.registry.impl.IdAutoIncreaseRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;

// TODO:
public class SimpleBlockRegistry extends IdAutoIncreaseRegistry<Block> implements BlockRegistry {

    private Block air;

    public SimpleBlockRegistry() {
        super(Block.class);
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
}
