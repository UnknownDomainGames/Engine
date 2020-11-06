package engine.registry.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import engine.block.Block;
import engine.block.state.BlockState;
import engine.item.BlockItem;
import engine.item.Item;
import engine.registry.RegistrationException;
import engine.registry.impl.SynchronizableIdRegistry;
import engine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;
import java.util.Optional;

public final class ItemRegistryImpl extends SynchronizableIdRegistry<Item> implements ItemRegistry {

    protected final BiMap<Block, BlockItem> blockToCorrItem = HashBiMap.create();

    public ItemRegistryImpl() {
        super(Item.class);
    }

    @Override
    public Optional<BlockItem> getBlockItem(BlockState block) {
        return Optional.ofNullable(blockToCorrItem.get(block));
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

    @Override
    public void handleRegistrySync(PacketSyncRegistry packet) {
        packet.getIdMap().forEach((key, value) -> {
            Item item = getValue(key);
            if (item != null) setId(item, value);
        });
    }
}
