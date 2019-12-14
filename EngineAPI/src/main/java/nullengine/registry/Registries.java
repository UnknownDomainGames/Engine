package nullengine.registry;

import nullengine.block.Block;
import nullengine.entity.EntityProvider;
import nullengine.item.Item;
import nullengine.registry.game.BlockRegistry;
import nullengine.registry.game.EntityRegistry;
import nullengine.registry.game.ItemRegistry;
import nullengine.util.function.Suppliers;
import nullengine.world.WorldProvider;

import java.util.function.Supplier;

public final class Registries {

    private static Supplier<RegistryManager> registryManager = () -> {
        throw new IllegalStateException("RegistryManager is uninitialized");
    };
    private static Supplier<Registry<WorldProvider>> worldProviderRegistry = () -> {
        throw new IllegalStateException("WorldProvider registry is uninitialized");
    };
    private static Supplier<BlockRegistry> blockRegistry = () -> {
        throw new IllegalStateException("Block registry is uninitialized");
    };
    private static Supplier<ItemRegistry> itemRegistry = () -> {
        throw new IllegalStateException("Item registry is uninitialized");
    };
    private static Supplier<EntityRegistry> entityRegistry = () -> {
        throw new IllegalStateException("Entity registry is uninitialized");
    };

    public static RegistryManager getRegistryManager() {
        return registryManager.get();
    }

    public static Registry<WorldProvider> getWorldProviderRegistry() {
        return worldProviderRegistry.get();
    }

    public static BlockRegistry getBlockRegistry() {
        return blockRegistry.get();
    }

    public static ItemRegistry getItemRegistry() {
        return itemRegistry.get();
    }

    public static EntityRegistry getEntityRegistry() {
        return entityRegistry.get();
    }

    public static void init(RegistryManager registryManager) {
        Registries.registryManager = Suppliers.ofWeakReference(registryManager);
        worldProviderRegistry = Suppliers.ofWeakReference(registryManager.getRegistry(WorldProvider.class).orElseThrow());
        blockRegistry = Suppliers.ofWeakReference((BlockRegistry) registryManager.getRegistry(Block.class).orElseThrow());
        itemRegistry = Suppliers.ofWeakReference((ItemRegistry) registryManager.getRegistry(Item.class).orElseThrow());
        entityRegistry = Suppliers.ofWeakReference((EntityRegistry) registryManager.getRegistry(EntityProvider.class).orElseThrow());
    }

    private Registries() {
    }
}
