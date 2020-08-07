package engine.registry.game;

import engine.block.Block;
import engine.registry.impl.IdAutoIncreaseRegistry;
import engine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class BlockRegistryImpl extends IdAutoIncreaseRegistry<Block> implements BlockRegistry {

    private Block air;

    public BlockRegistryImpl() {
        super(Block.class);
    }

    @Nonnull
    @Override
    public Block air() {
        return air;
    }

    @Override
    public void setAirBlock(@Nonnull Block air) {
        if (this.air != null)
            throw new IllegalStateException("Block air has been set");

        this.air = Objects.requireNonNull(air);
    }

    @Override
    public void handleRegistrySync(PacketSyncRegistry packet) {
        packet.getIdMap().forEach((key, value) -> {
            Block block = getValue(key);
            if (block != null) setId(block, value);
        });
    }
}
