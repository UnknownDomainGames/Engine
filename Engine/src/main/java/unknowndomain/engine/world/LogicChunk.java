package unknowndomain.engine.world;

import com.google.common.collect.Maps;
import unknowndomain.engine.Entity;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockRuntime;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LogicChunk implements Chunk {

    int[][] data = new int[16][16 * 16 * 16];
    private GameContext context;

    public LogicChunk(GameContext context) {
        this.context = context;
    }

    private List<Entity> entities = new ArrayList<>();
    private Map<BlockPos, Block> blockObjects = Maps.newHashMap();
    private Map<String, Object> components;

    @Override
    public Collection<Block> getRuntimeBlock() {
        return blockObjects.values();
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public Block getBlock(BlockPos pos) {
        Block object = blockObjects.get(pos);
        if (object != null) return object;
        if (pos.getY() < 0) {
            return context.getBlockRegistry().getValue(0);
        }

        int x = pos.getX() & 0xF;
        int y = pos.getY() & 0xF;
        int z = pos.getZ() & 0xF;

        int heightIndex = pos.getY() >> 4;
        int posIndex = (x << 8) | (y << 4) | z;
        int id = data[heightIndex][posIndex];
        return context.getBlockRegistry().getValue(id);
    }

    @Override
    public void setBlock(BlockPos pos, Block destBlock) {
        if (pos.getY() < 0) {
            return;
        }
        int x = pos.getX() & 0xF;
        int y = pos.getY() & 0xF;
        int z = pos.getZ() & 0xF;


        Block prev = blockObjects.get(pos);
        if (prev != null) {
            blockObjects.remove(pos);
        }

        int id = 0;
        if (destBlock != null) {
            if (destBlock instanceof BlockRuntime) {
                blockObjects.put(pos, destBlock);
            }
            id = context.getBlockRegistry().getId(destBlock);
        }

        data[pos.getY() >> 4][(x << 8) | (y << 4) | z] = id;

        context.post(new BlockChange(pos, id));
    }

    public void addEntity(Entity entity) {

    }

    public void removeEntity(Entity entity) {

    }

    public static class BlockChange implements Event {
        public final BlockPos pos;
        public final int blockId;

        public BlockChange(BlockPos pos, int blockId) {
            this.pos = pos;
            this.blockId = blockId;
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getComponent(@Nonnull String name) {
        return (T) components.get(name);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getComponent(@Nonnull Class<T> type) {
        return (T) components.get(type.getName());
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }
}
