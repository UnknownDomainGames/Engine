package unknowndomain.engine.registry;

import com.google.common.collect.HashBiMap;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.apache.commons.lang3.Validate;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleIdentifiedRegistry<T extends RegistryEntry<T>> extends SimpleRegistry<T> implements IdentifiedRegistry<T> {
    private IntObjectMap<T> idToObject = new IntObjectHashMap<>();
    private Map<String, Integer> nameToId = HashBiMap.create();
    private AtomicInteger id = new AtomicInteger();

    @Override
    public int getId(T obj) {
        Validate.notNull(obj);
        return nameToId.get(obj.getRegistryName());
    }

    @Override
    public int getId(String key) {
        Validate.notNull(key);
        return nameToId.get(key);
    }

    @Override
    public String getKey(int id) {
        return idToObject.get(id).getRegistryName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }

    @Override
    public T register(T obj) {
        super.register(obj);
        int next = id.getAndIncrement();
        idToObject.put(next, obj);
        nameToId.put(obj.getRegistryName(), next);
        return obj;
    }

    //    @Override
//    public Collection<Map.Entry<ResourcePath, T>> getEntries() {
//        return null;
//    }
}
