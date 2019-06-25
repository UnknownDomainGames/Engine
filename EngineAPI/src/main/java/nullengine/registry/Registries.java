package nullengine.registry;

import nullengine.block.Block;
import nullengine.client.block.ClientBlock;
import nullengine.registry.game.BlockRegistry;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public final class Registries {

    private static WeakReference<BlockRegistry> blockRegistry;
    private static WeakReference<Registry<ClientBlock>> clientBlockRegistry;

    @Nullable
    public static BlockRegistry getBlockRegistry() {
        return blockRegistry != null ? blockRegistry.get() : null;
    }

    @Nullable
    public static Registry<ClientBlock> getClientBlockRegistry() {
        return clientBlockRegistry != null ? clientBlockRegistry.get() : null;
    }

    public static void init(RegistryManager registryManager) {
        blockRegistry = new WeakReference<>((BlockRegistry) registryManager.getRegistry(Block.class));
        clientBlockRegistry = new WeakReference<>(registryManager.getRegistry(ClientBlock.class));
    }

    private Registries() {
    }
}
