package unknowndomain.game.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.item.BlockItem;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.RegistrationException;
import unknowndomain.engine.registry.game.ItemRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleItemRegistry implements ItemRegistry {

    protected final BiMap<String, Item> nameToObject = HashBiMap.create();
    protected final BiMap<Integer, Item> idToObject = HashBiMap.create();
    private final AtomicInteger nextId = new AtomicInteger(0);
    protected final BiMap<Block, BlockItem> blockToCorrItem = HashBiMap.create();
    @Override
    public BlockItem getItemBlock(Block block) {
        return blockToCorrItem.get(block);
    }

    @Override
    public boolean hasItemBlock(Block block) {
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
        if (nameToObject.containsKey(obj.getRegisterName())) {
            throw new RegistrationException(String.format("Item id:%s has already registered!", obj.getRegisterName()));
        }
        nameToObject.put(obj.getRegisterName(), obj);
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
}
