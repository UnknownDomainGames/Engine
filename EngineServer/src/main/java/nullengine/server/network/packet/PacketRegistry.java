package nullengine.server.network.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nullengine.Platform;
import nullengine.event.Listener;
import nullengine.registry.impl.IdAutoIncreaseRegistry;
import nullengine.server.event.PacketReceivedEvent;

public class PacketRegistry extends IdAutoIncreaseRegistry<Packet> {

    //Key: local id    Value: Remapped id (sync to server's id)
    private final BiMap<Integer, Integer> mapping = HashBiMap.create();

    public PacketRegistry() {
        super(Packet.class);
        Platform.getEngine().getEventBus().register(this);
    }

    @Listener
    public void onMappingPacketReceived(PacketReceivedEvent event){

    }

    @Override
    public int getId(Packet obj) {
        return getId(obj, true);
    }

    public int getId(Packet obj, boolean remapped){
        int id = super.getId(obj);
        if(remapped){
            return mapping.getOrDefault(id,id);
        }
        return id;
    }

    @Override
    public int getId(String key) {
        return getId(key, true);
    }

    public int getId(String key, boolean remapped){
        int id = super.getId(key);
        if(remapped){
            return mapping.getOrDefault(id,id);
        }
        return id;
    }

    @Override
    public String getKey(int id) {
        return getKey(id, true);
    }

    public String getKey(int id, boolean fromRemapped){
        if(fromRemapped){
            id = mapping.inverse().getOrDefault(id, id);
        }
        return super.getKey(id);
    }

    @Override
    public Packet getValue(int id) {
        return getValue(id, true);
    }

    public Packet getValue(int id, boolean fromRemapped){
        if(fromRemapped){
            id = mapping.inverse().getOrDefault(id, id);
        }
        return super.getValue(id);
    }
}
