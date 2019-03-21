package unknowndomain.engine.registry;

import unknowndomain.engine.block.Block;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public final class Registries {

    private static WeakReference<Registry<Block>> blockRegistry;

    @Nullable
    public static Registry<Block> getBlockRegistry() {
        return blockRegistry != null ? blockRegistry.get() : null;
    }

    public static void init(RegistryManager registryManager) {
        blockRegistry = new WeakReference<>(registryManager.getRegistry(Block.class));
    }

    private Registries() {
    }
}
