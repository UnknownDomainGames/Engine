package unknowndomain.engine.registry.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.registry.Registry;

public interface BlockRegistry extends Registry<Block> {

    Block air();
}
