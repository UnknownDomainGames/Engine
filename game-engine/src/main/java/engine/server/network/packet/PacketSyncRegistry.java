package engine.server.network.packet;

import engine.registry.Name;
import engine.registry.Registry;
import engine.registry.StateSerializableRegistry;
import engine.registry.SynchronizableRegistry;
import engine.server.network.PacketBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketSyncRegistry implements Packet {

    private Name registryName;
    private Target target;
    private Map<String, Integer> idMap;

    public PacketSyncRegistry() {
    }

    public PacketSyncRegistry(Registry<?> registry, Target target) {
        this.registryName = registry.getRegistryName();
        this.target = target;
        this.idMap = target.collector.apply(registry);
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeString(registryName.toString());
        buf.writeEnum(target);
        buf.writeVarInt(idMap.size());
        for (var entry : idMap.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        registryName = Name.fromString(buf.readString());
        target = buf.readEnum(Target.class);
        var size = buf.readVarInt();
        idMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            var key = buf.readString();
            var value = buf.readVarInt();
            idMap.put(key, value);
        }
    }

    public Name getRegistryName() {
        return registryName;
    }

    public Target getTarget() {
        return target;
    }

    public Map<String, Integer> getIdMap() {
        return idMap;
    }

    public enum Target {

        REGISTRY_ID((registry) -> {
            var map = new HashMap<String, Integer>(registry.getEntries().size());
            for (var key : registry.getKeys()) {
                map.put(key, registry.getId(key));
            }
            return map;
        }, ((registry, idMap) -> {
            ((SynchronizableRegistry<?>) registry).sync(idMap);
        })),
        STATE_ID((registry) -> {
            return ((StateSerializableRegistry<?, ?>) registry).collectToSerializedIdMap();
        }, ((registry, idMap) -> {
            ((StateSerializableRegistry<?, ?>) registry).synchronizeIdFromSerializedIdMap(idMap);
        }));

        public Function<Registry<?>, Map<String, Integer>> collector;
        public BiConsumer<Registry<?>, Map<String, Integer>> applier;

        Target(Function<Registry<?>, Map<String, Integer>> collector, BiConsumer<Registry<?>, Map<String, Integer>> applier) {
            this.collector = collector;
            this.applier = applier;
        }
    }
}
