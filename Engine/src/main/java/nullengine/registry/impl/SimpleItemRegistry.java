package nullengine.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nullengine.block.Block;
import nullengine.event.Listener;
import nullengine.item.BlockItem;
import nullengine.item.Item;
import nullengine.registry.RegistrationException;
import nullengine.registry.game.ItemRegistry;
import nullengine.server.event.PacketReceivedEvent;
import nullengine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

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

    @Listener
    public void onMappingPacketReceived(PacketReceivedEvent<PacketSyncRegistry> event){
        if(event.getPacket().getRegistryName().equals(this.getRegistryName())){
            var entries = getEntries().stream().sorted(Comparator.comparingInt(entry->entry.getValue().getId())).collect(Collectors.toList());
            var idMap = HashBiMap.create(event.getPacket().getIdMap()).inverse();
            var list1 = new ArrayList<Item>();
            for (int i = 0; i < entries.size(); i++) {
                if(idMap.containsKey(i)){
                    final String key = idMap.get(i);
                    var itemEntry = entries.stream().filter(entry -> entry.getKey() == key).findFirst().get();
                    list1.add(i, itemEntry.getValue());
                    entries.remove(itemEntry);
                }
            }
            for (int i = 0; i < nameToObject.size(); i++) {
                if (!idMap.containsKey(i)) {
                    list1.add(i, entries.get(0).getValue());
                    entries.remove(0);
                }
            }
            idToObject.clear();
            for (int i = 0; i < list1.size(); i++) {
                setId(list1.get(i), i);
            }
        }
    }
}
