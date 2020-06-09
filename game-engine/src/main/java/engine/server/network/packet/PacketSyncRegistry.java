package engine.server.network.packet;

import engine.registry.Name;
import engine.registry.Registry;
import engine.server.network.PacketBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PacketSyncRegistry implements Packet {

    private Name registryName;
    private Map<String, Integer> idMap;

    public PacketSyncRegistry(){}

    public PacketSyncRegistry(Registry registry){
        registryName = registry.getRegistryName();
        idMap = new HashMap<>();
        for (var key : registry.getKeys()) {
            idMap.put((String)key, registry.getId((String)key));
        }
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeString(registryName.toString());
        buf.writeVarInt(idMap.size());
        for (var entry : idMap.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        registryName = Name.fromString(buf.readString());
        var size = buf.readVarInt();
        idMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            var key = buf.readString();
            var value = buf.readVarInt();
            idMap.put(key,value);
        }
    }

    public Name getRegistryName() {
        return registryName;
    }

    public Map<String, Integer> getIdMap() {
        return idMap;
    }
}
