package engine.registry.game;

import engine.event.Listener;
import engine.registry.Name;
import engine.registry.RegistrationException;
import engine.registry.impl.SynchronizableIdRegistry;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.Packet;
import engine.server.network.packet.PacketProvider;
import engine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;

public class PacketRegistry extends SynchronizableIdRegistry<PacketProvider> {

    //Key: local id    Value: Remapped id (sync to server's id)
//    private final BiMap<Integer, Integer> mapping = HashBiMap.create();

    public PacketRegistry() {
        super(PacketProvider.class);
//        Platform.getEngine().getEventBus().register(this);
    }

    @Nonnull
    @Override
    public PacketProvider register(@Nonnull PacketProvider obj) {
        if (getValues().stream().anyMatch(packet -> packet.getPacketType() == obj.getPacketType()))
            throw new RegistrationException(String.format("Packet %s is already registered", obj.getClass().getSimpleName()));
        return super.register(obj);
    }

    @Listener
    public void onMappingPacketReceived(PacketReceivedEvent<PacketSyncRegistry> event) {
//        if(event.getPacket().getRegistryName().equals(this.getRegistryName())){
//            for (var entry : event.getPacket().getIdMap().entrySet()) {
//                var local = getId(entry.getKey(), false);
//                if(!entry.getKey().equals(getKey(local, false))){ // true if this name is actually not registered
//                    continue;
//                }
//                mapping.put(local, entry.getValue());
//            }
//        }
    }

    public int getId(Packet packet) {
        return getId(packet, true);
    }

    public int getId(Packet packet, boolean remapped) {
        Class<? extends Packet> packetType = packet.getClass();
        for (PacketProvider provider : this) {
            if (packetType == provider.getPacketType()) {
                return getId(provider);
            }
        }
        return 0;
    }

//    @Override
//    public int getId(PacketProvider obj) {
//        return getId(obj, true);
//    }
//
//    public int getId(PacketProvider obj, boolean remapped) {
//        var optional = getEntries().stream()
//                .filter(entry -> entry.getValue().getClass() == obj.getClass())
//                .findFirst();
//        return optional.map(entry -> getId(entry.getValue())).orElse(0);
////        int id = optional.map(entry -> getValue(entry.getKey()).getId()).get();
////        if (remapped) {
////            return mapping.getOrDefault(id, id);
////        }
////        return id;
//    }

    @Override
    public int getId(String key) {
        return getId(key, true);
    }

    public int getId(String key, boolean remapped) {
        int id = super.getId(key);
//        if(remapped){
//            return mapping.getOrDefault(id,id);
//        }
        return id;
    }

    @Override
    public Name getKey(int id) {
        return getKey(id, true);
    }

    public Name getKey(int id, boolean fromRemapped) {
//        if(fromRemapped){
//            id = mapping.inverse().getOrDefault(id, id);
//        }
        return super.getKey(id);
    }

    @Override
    public PacketProvider getValue(int id) {
        return getValue(id, true);
    }

    public PacketProvider getValue(int id, boolean fromRemapped) {
//        if(fromRemapped){
//            id = mapping.inverse().getOrDefault(id, id);
//        }
        return super.getValue(id);
    }
}
