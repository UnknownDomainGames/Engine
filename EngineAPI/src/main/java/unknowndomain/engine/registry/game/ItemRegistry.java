package unknowndomain.engine.registry.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.registry.Registry;

public interface ItemRegistry extends Registry<Item> {

    ItemBlock getItemBlock(Block block);
}
