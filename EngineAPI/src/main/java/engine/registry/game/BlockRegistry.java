package engine.registry.game;

import engine.block.Block;
import engine.registry.Registry;

import javax.annotation.Nonnull;

public interface BlockRegistry extends Registry<Block> {

    @Nonnull
    Block air();

    void setAirBlock(@Nonnull Block air);
}
