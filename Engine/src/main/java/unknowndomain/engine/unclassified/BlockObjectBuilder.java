package unknowndomain.engine.unclassified;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.joml.AABBd;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockObjectBuilder {
    private AABBd boundingBox = new AABBd(0, 0, 0, 1, 1, 1);

    private Block.PlaceBehavior placeBehavior;
    private Block.ActiveBehavior activeBehavior;
    private Block.TouchBehavior touchBehavior;
    private Block.DestroyBehavior destroyBehavior;

    public static BlockObjectBuilder create() {
        return new BlockObjectBuilder();
    }

    private Map<String, Object> map = new HashMap<>();

    private List<Block.Property<?>> properties = new ArrayList<>();
    private ResourcePath path;

    public BlockObjectBuilder setBoundingBox(AABBd boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    public BlockObjectBuilder setPath(ResourcePath path) {
        this.path = path;
        return this;
    }

    public BlockObjectBuilder setPlaceBehavior(Block.PlaceBehavior placeBehavior) {
        this.placeBehavior = placeBehavior;
        return this;
    }

    public BlockObjectBuilder setActiveBehavior(Block.ActiveBehavior activeBehavior) {
        this.activeBehavior = activeBehavior;
        return this;
    }

    public BlockObjectBuilder setTouchBehavior(Block.TouchBehavior touchBehavior) {
        this.touchBehavior = touchBehavior;
        return this;
    }

    public BlockObjectBuilder setDestroyBehavior(Block.DestroyBehavior destroyBehavior) {
        this.destroyBehavior = destroyBehavior;
        return this;
    }

    public BlockObjectBuilder addProperty(Block.Property<?> prop) {
        this.properties.add(prop);
        return this;
    }

    public BlockObjectBuilder setMap(Map<String, Object> map) {
        this.map = map;
        return this;
    }

    private List<ImmutableMap<Block.Property<?>, Comparable<?>>> compute(Block.Property<?>[] props) {
        List<ImmutableMap<Block.Property<?>, Comparable<?>>> ls = new ArrayList<>();
        compute(new HashMap<>(), props, 0, ls);
        return ls;
    }

    // combination of prop and value
    private void compute(Map<Block.Property<?>, Comparable<?>> map, Block.Property<?>[] props, int index, List<ImmutableMap<Block.Property<?>, Comparable<?>>> ls) {
        if (index < props.length) {
            Block.Property<?> prop = props[index];
            for (Comparable<?> v : prop.getValues()) {
                map.put(prop, v);
                compute(map, props, index + 1, ls);
                map.remove(prop);
            }
        } else {
            ls.add(ImmutableMap.copyOf(map));
        }
    }

    public BlockObject build() {
        return new BlockObjectShared(boundingBox, placeBehavior, activeBehavior, touchBehavior, destroyBehavior, null).setRegistryName(path);
    }

    public List<BlockObject> buildAll() {
//        if (this.map != null)
//            return Lists.newArrayList(new BlockObjectRuntime(block, placeBehavior, activeBehavior, touchBehavior, destroyBehavior, map));
        Block.Property<?>[] props = this.properties.toArray(new Block.Property[this.properties.size()]);
        List<ImmutableMap<Block.Property<?>, Comparable<?>>> compute = this.compute(props);
        ImmutableTable.Builder<Block.Property<?>, Comparable<?>, BlockObjectShared> builder = ImmutableTable.builder();

        List<BlockObjectShared> collect = compute.stream().map(m -> new BlockObjectShared(boundingBox, placeBehavior, activeBehavior, touchBehavior, destroyBehavior, m)).collect(Collectors.toList());

        for (BlockObjectShared shared : collect) {
            ImmutableMap<Block.Property<?>, Comparable<?>> properties = shared.getProperties();
            for (Map.Entry<Block.Property<?>, Comparable<?>> entry : properties.entrySet()) {
                builder.put(entry.getKey(), entry.getValue(), shared);
            }
        }
        ImmutableTable<Block.Property<?>, Comparable<?>, BlockObjectShared> table = builder.build();
        for (BlockObjectShared shared : collect) {
            shared.propertiesTable = table;
        }
        return (List<BlockObject>) (Object) collect;
    }
}