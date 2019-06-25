package nullengine.registry.game;

import nullengine.block.Block;
import nullengine.registry.Registry;

import javax.annotation.Nonnull;

public interface BlockRegistry extends Registry<Block> {

    @Nonnull
    Block air();

    void setAirBlock(@Nonnull Block air);
}
