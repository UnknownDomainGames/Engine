package unknowndomain.game.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.registry.RegisterException;
import unknowndomain.engine.registry.game.ItemRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SimpleItemRegistry implements ItemRegistry {

    protected final BiMap<String, Item> nameToObject = HashBiMap.create();
    protected final BiMap<Block, ItemBlock> blockToCorrItem = HashBiMap.create();
    @Override
    public ItemBlock getItemBlock(Block block) {
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

    @Override
    public Item register(@Nonnull Item obj) throws RegisterException {
        if(nameToObject.containsKey(obj.getLocalName())){
            throw new RegisterException(String.format("Item id:%s has already registered!", obj.getLocalName()));
        }
        nameToObject.put(obj.getLocalName(), obj);
        if(obj instanceof ItemBlock){
            ItemBlock itemBlock = (ItemBlock) obj;
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
//        return 0; TODO: id?
        throw new UnsupportedOperationException("SimpleItemRegistry is not responsible for id matching");
    }

    @Override
    public int getId(String key) {
//        return 0; TODO: id?
        throw new UnsupportedOperationException("SimpleItemRegistry is not responsible for id matching");
    }

    @Override
    public String getKey(int id) {
//        return ""; TODO: id?
        throw new UnsupportedOperationException("SimpleItemRegistry is not responsible for id matching");
    }

    @Override
    public Item getValue(int id) {
//        return null; TODO: id?
        throw new UnsupportedOperationException("SimpleItemRegistry is not responsible for id matching");
    }

    @Override
    public Collection<Map.Entry<String, Item>> getEntries() {
        return nameToObject.entrySet();
    }
}
