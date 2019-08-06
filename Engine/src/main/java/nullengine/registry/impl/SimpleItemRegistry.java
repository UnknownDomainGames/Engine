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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimpleItemRegistry implements ItemRegistry {

    protected final BiMap<String, Item> nameToObject = HashBiMap.create();
    protected final BiMap<Integer, Item> idToObject = HashBiMap.create();
    private final AtomicInteger nextId = new AtomicInteger(0);
    protected final BiMap<Block, BlockItem> blockToCorrItem = HashBiMap.create();

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
    public Class<Item> getEntryType() {
        return Item.class;
    }

    @Nonnull
    @Override
    public String getRegistryName() {
        return "item";
    }

    @Nonnull
    @Override
    public Item register(@Nonnull Item obj) throws RegistrationException {
        if (nameToObject.containsKey(obj.getName())) {
            throw new RegistrationException(String.format("Item id:%s has already registered!", obj.getName()));
        }
        nameToObject.put(obj.getName(), obj);
        setId(obj, nextId.getAndIncrement());
        if (obj instanceof BlockItem) {
            BlockItem itemBlock = (BlockItem) obj;
            blockToCorrItem.put((itemBlock).getBlock(), itemBlock);
        }
        return obj;
    }

    @Override
    public boolean containsValue(Item value) {
        return nameToObject.containsValue(value);
    }

    @Override
    public Item getValue(String registryName) {
        return nameToObject.get(registryName);
    }

    @Override
    public String getKey(Item value) {
        return nameToObject.inverse().get(value);
    }

    @Override
    public boolean containsKey(String key) {
        return nameToObject.containsKey(key);
    }

    @Override
    public Collection<Item> getValues() {
        return nameToObject.values();
    }

    @Override
    public Set<String> getKeys() {
        return nameToObject.keySet();
    }

    @Override
    public int getId(Item obj) {
        return idToObject.inverse().get(obj);
    }

    @Override
    public int getId(String key) {
        return idToObject.inverse().get(nameToObject.get(key));
    }

    @Override
    public String getKey(int id) {
        return nameToObject.inverse().get(idToObject.get(id));
    }

    @Override
    public Item getValue(int id) {
        return idToObject.get(id);
    }

    protected void setId(Item entry, int id) {
        idToObject.put(id, entry);
    }

    @Override
    public Collection<Map.Entry<String, Item>> getEntries() {
        return nameToObject.entrySet();
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
