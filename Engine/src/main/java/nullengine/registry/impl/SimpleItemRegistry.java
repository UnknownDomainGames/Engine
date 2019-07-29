package nullengine.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nullengine.block.Block;
import nullengine.item.BlockItem;
import nullengine.item.Item;
import nullengine.registry.RegistrationException;
import nullengine.registry.game.ItemRegistry;

import javax.annotation.Nonnull;

public class SimpleItemRegistry extends IdBakeRegistry<Item> implements ItemRegistry {

    protected final BiMap<Block, BlockItem> blockToCorrItem = HashBiMap.create();

    public SimpleItemRegistry() {
        super(Item.class, "id");
    }

    @Override
    public BlockItem getBlockItem(Block block) {
        return blockToCorrItem.get(block);
    }

    @Override
    public boolean hasBlockItem(Block block) {
        return blockToCorrItem.containsKey(block);
    }

    @Nonnull
    @Override
    public Item register(@Nonnull Item obj) throws RegistrationException {
        super.register(obj);
        if (obj instanceof BlockItem) {
            BlockItem itemBlock = (BlockItem) obj;
            blockToCorrItem.put((itemBlock).getBlock(), itemBlock);
        }
        return obj;
    }
}
