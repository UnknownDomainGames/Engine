package unknowndomain.engine.registry.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.registry.Registry;

import javax.annotation.Nullable;

public interface ItemRegistry extends Registry<Item> {

    @Nullable
    ItemBlock getItemBlock(Block block);

    boolean hasItemBlock(Block block);
}
