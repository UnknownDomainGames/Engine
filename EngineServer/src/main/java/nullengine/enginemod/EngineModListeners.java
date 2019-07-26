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
import nullengine.registry.game.BlockRegistry;
import nullengine.registry.impl.IdAutoIncreaseRegistry;
import nullengine.registry.impl.SimpleBlockRegistry;
import nullengine.registry.impl.SimpleEntityRegistry;
import nullengine.registry.impl.SimpleItemRegistry;
import nullengine.server.network.packet.Packet;
import nullengine.server.network.packet.PacketHandshake;
import nullengine.server.network.packet.PacketRaw;
import nullengine.world.WorldProvider;

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
        e.addRegistry(Packet.class, () -> new IdAutoIncreaseRegistry<>(Packet.class));
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
    }
}
