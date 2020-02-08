package engine.registry.game;

import engine.block.Block;
import engine.item.BlockItem;
import engine.item.Item;
import engine.registry.Registry;

import java.util.Optional;

public interface ItemRegistry extends Registry<Item> {

    Optional<BlockItem> getBlockItem(Block block);

    boolean hasBlockItem(Block block);
}
