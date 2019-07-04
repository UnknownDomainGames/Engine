package nullengine.registry;

import nullengine.block.Block;
import nullengine.exception.UninitializationException;
import nullengine.item.Item;
import nullengine.registry.game.BlockRegistry;
import nullengine.registry.game.ItemRegistry;
import nullengine.util.Suppliers;

import java.util.function.Supplier;

public final class Registries {

    private static Supplier<BlockRegistry> blockRegistry = UninitializationException.supplier("Block registry is uninitialized");
    private static Supplier<ItemRegistry> itemRegistry = UninitializationException.supplier("Item registry is uninitialized");

    public static BlockRegistry getBlockRegistry() {
        return blockRegistry.get();
    }

    public static ItemRegistry getItemRegistry() {
        return itemRegistry.get();
    }

    public static void init(RegistryManager registryManager) {
        blockRegistry = Suppliers.ofWeakReference((BlockRegistry) registryManager.getRegistry(Block.class));
        itemRegistry = Suppliers.ofWeakReference((ItemRegistry) registryManager.getRegistry(Item.class));
    }

    private Registries() {
    }
}
