package unknowndomain.engine.unclassified;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.registry.IdentifiedRegistry;
import unknowndomain.engine.api.unclassified.Block;
import unknowndomain.engine.api.unclassified.BlockObject;
import unknowndomain.engine.api.world.Chunk;

public class ChunkImpl implements Chunk {
    private IdentifiedRegistry<BlockObject> reg;

    private int[][] data;
    private List<Entity> entities = new ArrayList<>();
    private Map<BlockPos, BlockObject> blockObjects = Maps.newHashMap();
    private Map<String, Object> components;

    public void tick() {
        for (Entity entity : entities) {
            entity.tick();
        }
        for (BlockObject object : blockObjects.values()) {
            Block.TickBehavior behavior = object.getBehavior(Block.TickBehavior.class);
            if (behavior != null) {
                behavior.tick(object);
            }
        }
    }

    @Override
    public BlockObject getBlock(BlockPos pos) {
        BlockObject object = blockObjects.get(pos);
        if (object != null) return object;

        int x = pos.getX() & this.getXSize();
        int y = pos.getY() & this.getYSize();
        int z = pos.getZ() & this.getZSize();

        return reg.getValue(data[y][x << 16 | z << 16 >> 16]);
    }

    @Override
    public void setBlock(BlockPos pos, BlockObject destBlock) {
        int x = pos.getX() & this.getXSize();
        int y = pos.getY() & this.getYSize();
        int z = pos.getZ() & this.getZSize();

        BlockObject prev = blockObjects.get(pos);
        if (prev != null) {
            blockObjects.remove(pos);
        }

        if (destBlock instanceof BlockObjectRuntime) {
            blockObjects.put(pos, destBlock);
        }

        data[y][x << 16 | z << 16 >> 16] = reg.getId(destBlock);
    }

//    @Override
//    public <Context extends RuntimeObject> Prototype<? extends RuntimeObject, Context> getPrototype() {
//        return null;
//    }

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
