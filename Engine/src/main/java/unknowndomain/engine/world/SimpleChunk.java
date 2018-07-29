package unknowndomain.engine.world;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.unclassified.BlockObject;
import unknowndomain.engine.api.unclassified.Prototype;
import unknowndomain.engine.api.unclassified.RuntimeObject;
import unknowndomain.engine.api.world.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SimpleChunk implements Chunk {
    private Map<BlockPos, Block> blockMap;

    public SimpleChunk(){
        blockMap = new HashMap<>();
    }

    @Override
    public BlockObject getBlock(BlockPos pos) {
        return null;
    }

    @Override
    public void setBlock(BlockPos pos, BlockObject destBlock) {

    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }
}
