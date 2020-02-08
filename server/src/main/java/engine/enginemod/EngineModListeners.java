package engine.enginemod;

import engine.Platform;
import engine.block.AirBlock;
import engine.block.Block;
import engine.entity.CameraEntity;
import engine.entity.EntityProvider;
import engine.entity.item.ItemEntity;
import engine.event.Listener;
import engine.event.mod.ModLifecycleEvent;
import engine.event.mod.ModRegistrationEvent;
import engine.item.Item;
import engine.registry.game.BlockRegistry;
import engine.registry.impl.*;
import engine.server.network.packet.*;
import engine.world.WorldProvider;

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
    public static void registerEntities(ModRegistrationEvent.Register<EntityProvider> event) {
        event.register(new EntityProvider(CameraEntity.class, CameraEntity::new, null).name("camera"));
        event.register(new EntityProvider(ItemEntity.class, ItemEntity::new, null).name("item"));
    }

    @Listener
    public static void registerPacket(ModRegistrationEvent.Register<PacketProvider> event){
        event.register(new PacketProvider.Builder().type(PacketRaw.class).name("raw").build());
        event.register(new PacketProvider.Builder().type(PacketHandshake.class).name("handshake").build());
        event.register(new PacketProvider.Builder().type(PacketDisconnect.class).name("disconnect").build());
        event.register(new PacketProvider.Builder().type(PacketSyncRegistry.class).name("registry-sync").build());
    }

}
