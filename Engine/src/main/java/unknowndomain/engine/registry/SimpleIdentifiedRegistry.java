package unknowndomain.engine.registry;

import com.google.common.collect.HashBiMap;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.SimpleRegistry;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleIdentifiedRegistry<T extends RegistryEntry<T>> extends SimpleRegistry<T>
        implements IdentifiedRegistry<T> {

    private final IntObjectMap<T> idToObject = new IntObjectHashMap<>();
    private final Map<ResourcePath, Integer> nameToId = HashBiMap.create();

    private final AtomicInteger nextId = new AtomicInteger(0);

    public SimpleIdentifiedRegistry() {
    }

    // or IntObjectMap<T>
    public SimpleIdentifiedRegistry(Map<ResourcePath, Integer> nameToId) {
        this.nameToId.putAll(nameToId);
        nextId.set(nameToId.values().stream().max((o1, o2) -> o2 - o1).get() + 1);        
    }    

    @Override
    public int getId(T obj) {
        Validate.notNull(obj);
        return nameToId.get(obj.getRegistryName());
    }

    @Override
    public int getId(ResourcePath key) {
        Validate.notNull(key);
        return nameToId.get(key);
    }

    @Override
    public ResourcePath getKey(int id) {
        return idToObject.get(id).getRegistryName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }

    @Override
    public T register(T obj) {
        super.register(obj);
        int id; // Bad code.
        if(nameToId.containsKey(obj.getRegistryName())){ //
            id = nameToId.get(obj.getRegistryName()); // wait, super.register should already through
        } else {
            id = this.nextId.getAndIncrement(); 
            nameToId.put(obj.getRegistryName(), id);
        }
        idToObject.put(id, obj); 
        return obj;
    }

    @Override
    public Collection<Entry<ResourcePath, Integer>> getNameToId() {
        return nameToId.entrySet();
    }
    // @Override
    // public Collection<Map.Entry<ResourcePath, T>> getEntries() {
    // return null;
    // }
}
