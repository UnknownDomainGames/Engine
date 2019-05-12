package unknowndomain.engine.registry.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.item.BlockItem;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.Registry;

import javax.annotation.Nullable;

public interface ItemRegistry extends Registry<Item> {

    @Nullable
    BlockItem getItemBlock(Block block);

    boolean hasItemBlock(Block block);
}
