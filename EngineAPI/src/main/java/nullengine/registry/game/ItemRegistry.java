package nullengine.registry.game;

import nullengine.block.Block;
import nullengine.item.BlockItem;
import nullengine.item.Item;
import nullengine.registry.Registry;

import java.util.Optional;

public interface ItemRegistry extends Registry<Item> {

    Optional<BlockItem> getBlockItem(Block block);

    boolean hasBlockItem(Block block);
}
