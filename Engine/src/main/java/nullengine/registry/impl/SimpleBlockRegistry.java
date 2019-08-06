package nullengine.registry.impl;

import com.google.common.collect.HashBiMap;
import nullengine.block.Block;
import nullengine.event.Listener;
import nullengine.registry.game.BlockRegistry;
import nullengine.server.event.PacketReceivedEvent;
import nullengine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO:
public class SimpleBlockRegistry extends IdAutoIncreaseRegistry<Block> implements BlockRegistry {

    private Block air;

    public SimpleBlockRegistry() {
        super(Block.class);
    }

    @Nonnull
    @Override
    public Block air() {
        return air;
    }

    @Override
    public void setAirBlock(@Nonnull Block air) {
        if (this.air != null)
            throw new IllegalStateException("Block air has been set");

        this.air = Objects.requireNonNull(air);
    }

    @Listener
    public void onMappingPacketReceived(PacketReceivedEvent<PacketSyncRegistry> event){
        if(event.getPacket().getRegistryName().equals(this.getRegistryName())){
            var entries = getEntries().stream().sorted(Comparator.comparingInt(entry->entry.getValue().getId())).collect(Collectors.toList());
            var idMap = HashBiMap.create(event.getPacket().getIdMap()).inverse();
            var list1 = new ArrayList<Block>();
            for (int i = 0; i < entries.size(); i++) {
                if(idMap.containsKey(i)){
                    final String key = idMap.get(i);
                    var blockEntry = entries.stream().filter(entry -> entry.getKey() == key).findFirst().get();
                    list1.add(i, blockEntry.getValue());
                    entries.remove(blockEntry);
                }
            }
            for (int i = 0; i < nameToObject.size(); i++) {
                if (!idMap.containsKey(i)) {
                    list1.add(i, entries.get(0).getValue());
                    entries.remove(0);
                }
            }
            idToObject.clear();
            for (int i = 0; i < list1.size(); i++) {
                setId(list1.get(i), i);
            }
        }
    }
}
