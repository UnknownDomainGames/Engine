package engine.registry.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import engine.block.Block;
import engine.block.state.BlockState;
import engine.registry.impl.SynchronizableIdRegistry;
import engine.server.network.packet.PacketSyncRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class BlockRegistryImpl extends SynchronizableIdRegistry<Block> implements BlockRegistry {

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

    private BiMap<Integer, BlockState> stateIdMap;

    @Override
    public void reconstructStateId() {
        if (stateIdMap == null) {
            stateIdMap = HashBiMap.create();
        }
        stateIdMap.clear();
        AtomicInteger i = new AtomicInteger();
        getValues().forEach(block -> block.getStateManager().getStates().forEach(state -> stateIdMap.put(i.getAndIncrement(), state)));
    }

    @Override
    public int getStateId(BlockState state) {
        if (stateIdMap == null) {
            throw new IllegalStateException("try getting state id before mapping is constructed");
        }
        return stateIdMap.inverse().get(state);
    }

    @Override
    public BlockState getStateFromId(int id) {
        if (stateIdMap == null) {
            throw new IllegalStateException("try getting state before mapping is constructed");
        }
        return stateIdMap.get(id);
    }
}
