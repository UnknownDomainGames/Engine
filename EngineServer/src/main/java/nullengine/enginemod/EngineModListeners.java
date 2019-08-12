package nullengine.enginemod;

import nullengine.Platform;
import nullengine.block.AirBlock;
import nullengine.block.Block;
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
        e.addRegistry(PacketProvider.class, PacketRegistry::new);
    }

    @Listener
    public static void registerBlocks(ModRegistrationEvent.Register<Block> event) {
        event.register(AirBlock.AIR);
        ((BlockRegistry) event.getRegistry()).setAirBlock(AirBlock.AIR);
    }

    @Listener
    public static void registerPacket(ModRegistrationEvent.Register<PacketProvider> event){
        event.register(new PacketProvider.Builder().type(PacketRaw.class).name("raw").build());
        event.register(new PacketProvider.Builder().type(PacketHandshake.class).name("raw").build());
        event.register(new PacketProvider.Builder().type(PacketDisconnect.class).name("raw").build());
        event.register(new PacketProvider.Builder().type(PacketSyncRegistry.class).name("raw").build());
    }

}
