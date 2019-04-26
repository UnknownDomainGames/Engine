package unknowndomain.engine.registry.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.registry.Registry;

import javax.annotation.Nonnull;

public interface BlockRegistry extends Registry<Block> {

    @Nonnull
    Block air();

    void setAirBlock(@Nonnull Block air);
}
