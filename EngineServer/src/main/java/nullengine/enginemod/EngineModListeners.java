package nullengine.enginemod;

import nullengine.Platform;
import nullengine.block.AirBlock;
import nullengine.block.Block;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.rendering.block.BlockRenderer;
import nullengine.entity.EntityProvider;
import nullengine.event.Listener;
import nullengine.event.mod.ModLifecycleEvent;
import nullengine.event.mod.ModRegistrationEvent;
import nullengine.item.Item;
import nullengine.registry.Registry;
import nullengine.registry.game.BlockRegistry;
import nullengine.registry.impl.*;
import nullengine.server.event.PacketReceivedEvent;
import nullengine.server.network.packet.*;
import nullengine.world.WorldProvider;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class EngineModListeners {

    @Listener
    public static void onPreInit(ModLifecycleEvent.PreInitialization event) {
        Platform.getEngine().getEventBus().register(EngineModListeners.class);
    }

    @Listener
    public static void constructRegistry(ModRegistrationEvent.Construction e) {
        // TODO: move to common.
        e.addRegistry(WorldProvider.class, () -> new IdAutoIncreaseRegistry<>(WorldProvider.class));
        e.addRegistry(Block.class, SimpleBlockRegistry::new);
        e.addRegistry(Item.class, SimpleItemRegistry::new);
        e.addRegistry(EntityProvider.class, SimpleEntityRegistry::new);
        e.addRegistry(Packet.class, PacketRegistry::new);
    }

    @Listener
    public static void registerBlocks(ModRegistrationEvent.Register<Block> event) {
        event.register(AirBlock.AIR);
        ((BlockRegistry) event.getRegistry()).setAirBlock(AirBlock.AIR);
    }

    @Listener
    public static void registerPacket(ModRegistrationEvent.Register<Packet> event){
        event.register(new PacketRaw().name("raw"));
        event.register(new PacketHandshake().name("handshake"));
        event.register(new PacketDisconnect().name("disconnect"));
        event.register(new PacketSyncRegistry().name("registry_sync"));
    }

    @Listener
    public static void syncRegistryId(PacketReceivedEvent<PacketSyncRegistry> event){
        switch (event.getPacket().getRegistryName()) {
            case "block":
                var registry = Platform.getEngine().getRegistryManager().getRegistry(Block.class);
                var entries = registry.getEntries().stream().sorted(Comparator.comparingInt(entry->entry.getValue().getId())).collect(Collectors.toList());
                var idMap = event.getPacket().getIdMap();

        }
    }
}
