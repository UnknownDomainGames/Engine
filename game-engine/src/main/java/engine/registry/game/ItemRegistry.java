package engine.registry.game;

import engine.block.Block;
import engine.block.state.BlockState;
import engine.item.BlockItem;
import engine.item.Item;
import engine.registry.Registry;
import engine.registry.SynchronizableRegistry;
import engine.server.network.packet.PacketSyncRegistry;

import java.util.Optional;

public interface ItemRegistry extends SynchronizableRegistry<Item> {

    Optional<BlockItem> getBlockItem(BlockState block);

    boolean hasBlockItem(Block block);

    void handleRegistrySync(PacketSyncRegistry packet);
}
