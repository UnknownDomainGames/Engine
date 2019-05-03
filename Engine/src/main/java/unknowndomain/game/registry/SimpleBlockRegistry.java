package unknowndomain.game.registry;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.registry.game.BlockRegistry;
import unknowndomain.engine.registry.impl.SimpleRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SimpleBlockRegistry extends SimpleRegistry<Block> implements BlockRegistry {

    protected Block air;

    public SimpleBlockRegistry() {
        super(Block.class);
    }

    @Override
    public Block air() {
        return air;
    }

    @Override
    public void setAirBlock(@Nonnull Block air) {
        this.air = Objects.requireNonNull(air);
    }
}
