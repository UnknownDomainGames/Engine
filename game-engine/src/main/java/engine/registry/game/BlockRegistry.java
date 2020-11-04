package engine.registry.game;

import engine.block.Block;
import engine.block.state.BlockState;
import engine.registry.Registry;
import engine.server.network.packet.PacketSyncRegistry;
import engine.state.StateIncludedRegistry;

import javax.annotation.Nonnull;

public interface BlockRegistry extends Registry<Block>, StateIncludedRegistry<BlockState> {

    @Nonnull
    Block air();

    void setAirBlock(@Nonnull Block air);

    void handleRegistrySync(PacketSyncRegistry packet);
}
