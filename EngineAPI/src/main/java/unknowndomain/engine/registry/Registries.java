package unknowndomain.engine.registry;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.registry.game.BlockRegistry;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public final class Registries {

    private static WeakReference<BlockRegistry> blockRegistry;

    @Nullable
    public static BlockRegistry getBlockRegistry() {
        return blockRegistry != null ? blockRegistry.get() : null;
    }

    public static void init(RegistryManager registryManager) {
        blockRegistry = new WeakReference<>((BlockRegistry) registryManager.getRegistry(Block.class));
    }

    private Registries() {
    }
}
