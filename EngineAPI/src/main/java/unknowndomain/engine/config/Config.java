package unknowndomain.engine.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private final Map<String, Object> items = new ConcurrentHashMap<>();
    private final Map<String, Class> itemtypes = new ConcurrentHashMap<>();

    public Object getEntry(String key){
        return items.getOrDefault(key, null);
    }
    public <T> T getEntryCast(String key, Class<T> clazz){
        if(!clazz.isAssignableFrom(itemtypes.get(key))){
            throw new ClassCastException(String.format("Entry(key=%s) is not from class %s", key, clazz.getName()));
        }
        return (T)items.get(key);
    }

    public void setEntry(String key, Object value){
        if(!itemtypes.containsKey(key)){
            setEntryType(key,value.getClass());
        }
        else{
            if(!itemtypes.get(key).isInstance(value))
                return;
        }
        items.put(key,value);
    }

    public void setEntryType(String key, Class clazz){
        itemtypes.put(key, clazz);
        if(items.containsKey(key) && !clazz.isInstance(items.get(key))){
            items.put(key, null);
        }
    }

    public int getInt(String key){
        if(!items.containsKey(key))
            return 0;
        return getEntryCast(key, Integer.class);
    }
    public float getFloat(String key){
        if(!items.containsKey(key))
            return 0;
        return getEntryCast(key, Float.class);
    }
    public byte getByte(String key){
        if(!items.containsKey(key))
            return 0;
        return getEntryCast(key, Byte.class);
    }
    public String getString(String key){
        return getEntryCast(key, String.class);
    }
    public double getDouble(String key){
        if(!items.containsKey(key))
            return 0;
        return getEntryCast(key, Double.class);
    }

    public void setInt(String key, int i){
        setEntry(key, i);
    }

    public void clearEntry(){
        items.clear();
        itemtypes.clear();
    }

    public void clearEntryContent(){
        items.clear();
    }
}
