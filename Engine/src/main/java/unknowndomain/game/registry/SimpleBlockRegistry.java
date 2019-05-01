package unknowndomain.game.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.registry.RegisterException;
import unknowndomain.engine.registry.game.BlockRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleBlockRegistry implements BlockRegistry {

    protected final BiMap<String, Block> nameToObject = HashBiMap.create();
    protected final BiMap<Integer, Block> idToObject = HashBiMap.create();
    private final AtomicInteger nextId = new AtomicInteger(0);
    protected Block air;

    @Override
    public Block air() {
        return air;
    }

    @Override
    public void setAirBlock(@Nonnull Block air) {
        this.air = Objects.requireNonNull(air);
    }

    @Nonnull
    @Override
    public Class<Block> getEntryType() {
        return Block.class;
    }

    @Nonnull
    @Override
    public String getRegistryName() {
        return "block";
    }

    @Override
    public Block register(@Nonnull Block obj) throws RegisterException {
        if(nameToObject.containsKey(obj.getLocalName())){
            throw new RegisterException(String.format("Block id:%s has already registered!", obj.getLocalName()));
        }
        nameToObject.put(obj.getLocalName(), obj);
        setId(obj, nextId.getAndIncrement());
        return obj;
    }

    @Override
    public boolean containsValue(Block value) {
        return nameToObject.containsValue(value);
    }

    @Override
    public Block getValue(String registryName) {
        return nameToObject.get(registryName);
    }

    @Override
    public String getKey(Block value) {
        return nameToObject.inverse().get(value);
    }

    @Override
    public boolean containsKey(String key) {
        return nameToObject.containsKey(key);
    }

    @Override
    public Collection<Block> getValues() {
        return nameToObject.values();
    }

    @Override
    public Set<String> getKeys() {
        return nameToObject.keySet();
    }

    @Override
    public int getId(Block obj) {
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
    public Block getValue(int id) {
        return idToObject.get(id);
    }

    protected void setId(Block entry, int id) {
        idToObject.put(id, entry);
    }

    @Override
    public Collection<Map.Entry<String, Block>> getEntries() {
        return nameToObject.entrySet();
    }

}
