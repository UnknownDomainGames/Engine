package engine.registry.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import engine.block.Block;
import engine.event.Listener;
import engine.item.BlockItem;
import engine.item.Item;
import engine.registry.RegistrationException;
import engine.registry.impl.IdAutoIncreaseRegistry;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;
import java.util.Optional;

public final class ItemRegistryImpl extends IdAutoIncreaseRegistry<Item> implements ItemRegistry {

    protected final BiMap<Block, BlockItem> blockToCorrItem = HashBiMap.create();

    public ItemRegistryImpl() {
        super(Item.class, "id");
    }

    @Override
    public Optional<BlockItem> getBlockItem(Block block) {
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

    @Listener
    public void onMappingPacketReceived(PacketReceivedEvent<PacketSyncRegistry> event) {
        if (event.getPacket().getRegistryName().equals(getRegistryName())) {
            event.getPacket().getIdMap().forEach((key, value) -> {
                Item item = getValue(key);
                if (item != null) setId(item, value);
            });
        }
    }
}
