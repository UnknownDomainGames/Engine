package engine.server.network.packet;

import engine.registry.Registry;
import engine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PacketSyncRegistry implements Packet {

    private String registryName;
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
        buf.writeVarInt(registryName.length());
        buf.writeCharSequence(registryName, StandardCharsets.UTF_8);
        buf.writeVarInt(idMap.size());
        for (var entry : idMap.entrySet()) {
            buf.writeVarInt(entry.getKey().length());
            buf.writeCharSequence(entry.getKey(), StandardCharsets.UTF_8);
            buf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        var length = buf.readVarInt();
        registryName = buf.readCharSequence(length, StandardCharsets.UTF_8).toString();
        var size = buf.readVarInt();
        idMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            length = buf.readVarInt();
            var key = buf.readCharSequence(length, StandardCharsets.UTF_8).toString();
            var value = buf.readVarInt();
            idMap.put(key,value);
        }
    }

    public String getRegistryName() {
        return registryName;
    }

    public Map<String, Integer> getIdMap() {
        return idMap;
    }
}
