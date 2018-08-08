package unknowndomain.engine.unclassified;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.apache.commons.lang3.Validate;
import org.joml.AABBd;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.*;
import java.util.stream.Collectors;

public class BlockObjectBuilder {
    private AABBd boundingBox = new AABBd(0, 0, 0, 1, 1, 1);
    private ResourcePath path;
    private Map<String, Object> map = new HashMap<>();
    private List<Block.Property<?>> properties = new ArrayList<>();

    private Block.PlaceBehavior placeBehavior;
    private Block.ActiveBehavior activeBehavior;
    private Block.TouchBehavior touchBehavior;
    private Block.DestroyBehavior destroyBehavior;

    private BlockObjectBuilder(ResourcePath path) {
        this.path = path;
    }

    public static BlockObjectBuilder create(ResourcePath path) {
        Validate.notNull(path);
        return new BlockObjectBuilder(path);
    }

    public BlockObjectBuilder setBoundingBox(AABBd boundingBox) {
        this.boundingBox = boundingBox;
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
    private void compute(Map<Block.Property<?>, Comparable<?>> map, Block.Property<?>[] props, int index,
                         List<ImmutableMap<Block.Property<?>, Comparable<?>>> ls) {
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
        return new BlockObjectShared(boundingBox, placeBehavior, activeBehavior, touchBehavior, destroyBehavior, null)
                .setRegistryName(path);
    }

    public List<BlockObject> buildAll() {
        // if (this.map != null)
        // return Lists.newArrayList(new BlockObjectRuntime(block, placeBehavior,
        // activeBehavior, touchBehavior, destroyBehavior, map));
        Block.Property<?>[] props = this.properties.toArray(new Block.Property[this.properties.size()]);
        List<ImmutableMap<Block.Property<?>, Comparable<?>>> compute = this.compute(props);
        ImmutableTable.Builder<Block.Property<?>, Comparable<?>, BlockObjectShared> builder = ImmutableTable.builder();

        List<BlockObjectShared> collect = compute.stream().map(m -> {
            BlockObjectShared shared = new BlockObjectShared(boundingBox, placeBehavior, activeBehavior, touchBehavior,
                    destroyBehavior, m);
            List<Map.Entry<Block.Property<?>, Comparable<?>>> entries = new ArrayList<>(m.entrySet());
            entries.sort(Comparator.comparing(a -> a.getKey().getName()));
            StringBuilder postfix = new StringBuilder();
            for (Map.Entry<Block.Property<?>, Comparable<?>> entry : entries) {
                String name = entry.getKey().getName();
                String value = entry.getValue().toString();
                postfix.append(".").append(name).append("=").append(value);
            }
            shared.setRegistryName(new ResourcePath(this.path.getDomain(), this.path.getPath() + postfix));
            return shared;
        }).collect(Collectors.toList());

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