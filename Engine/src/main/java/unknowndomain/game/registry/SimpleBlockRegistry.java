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

public class SimpleBlockRegistry implements BlockRegistry {

    protected final BiMap<String, Block> nameToObject = HashBiMap.create();

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
//        return 0; TODO: id?
        throw new UnsupportedOperationException("SimpleBlockRegistry is not responsible for id matching");
    }

    @Override
    public int getId(String key) {
//        return 0; TODO: id?
        throw new UnsupportedOperationException("SimpleBlockRegistry is not responsible for id matching");
    }

    @Override
    public String getKey(int id) {
//        return ""; TODO: id?
        throw new UnsupportedOperationException("SimpleBlockRegistry is not responsible for id matching");
    }

    @Override
    public Block getValue(int id) {
//        return null; TODO: id?
        throw new UnsupportedOperationException("SimpleBlockRegistry is not responsible for id matching");
    }

    @Override
    public Collection<Map.Entry<String, Block>> getEntries() {
        return nameToObject.entrySet();
    }

}
