package unknowndomain.engine.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.apache.commons.lang3.Validate;
import org.joml.AABBd;

import java.util.*;
import java.util.stream.Collectors;

public class BlockBuilder {
    private AABBd boundingBox = new AABBd(0, 0, 0, 1, 1, 1);
    // private AABBd boundingBox = new AABBd(-.5, 0, -.5, .5, 1, .5);

    private List<AABBd> aabBds = new ArrayList<>();
    private String path;
    private Map<String, Object> map = new HashMap<>();
    private List<BlockPrototype.Property<?>> properties = new ArrayList<>();
    private boolean noCollision = false;

    private BlockPrototype.PlaceBehavior placeBehavior = BlockPrototype.DEFAULT_PLACE;
    private BlockPrototype.ActiveBehavior activeBehavior = BlockPrototype.DEFAULT_ACTIVE;
    private BlockPrototype.TouchBehavior touchBehavior = BlockPrototype.DEFAULT_TOUCH;
    private BlockPrototype.DestroyBehavior destroyBehavior = BlockPrototype.DEFAULT_DESTROY;

    private BlockBuilder(String path) {
        this.path = path;
    }

    public BlockBuilder setNoCollision() {
        this.noCollision = true;
        return this;
    }

    public static BlockBuilder create(String path) {
        Validate.notNull(path);
        return new BlockBuilder(path);
    }

    public BlockBuilder addBoundingBox(AABBd boundingBox) {
        aabBds.add(boundingBox);
        return this;
    }

    public BlockBuilder setPlaceBehavior(BlockPrototype.PlaceBehavior placeBehavior) {
        this.placeBehavior = placeBehavior;
        return this;
    }

    public BlockBuilder setActiveBehavior(BlockPrototype.ActiveBehavior activeBehavior) {
        this.activeBehavior = activeBehavior;
        return this;
    }

    public BlockBuilder setTouchBehavior(BlockPrototype.TouchBehavior touchBehavior) {
        this.touchBehavior = touchBehavior;
        return this;
    }

    public BlockBuilder setDestroyBehavior(BlockPrototype.DestroyBehavior destroyBehavior) {
        this.destroyBehavior = destroyBehavior;
        return this;
    }

    public BlockBuilder addProperty(BlockPrototype.Property<?> prop) {
        this.properties.add(prop);
        return this;
    }

    public BlockBuilder setMap(Map<String, Object> map) {
        this.map = map;
        return this;
    }

    private List<ImmutableMap<BlockPrototype.Property<?>, Comparable<?>>> compute(BlockPrototype.Property<?>[] props) {
        List<ImmutableMap<BlockPrototype.Property<?>, Comparable<?>>> ls = new ArrayList<>();
        compute(new HashMap<>(), props, 0, ls);
        return ls;
    }

    // combination of prop and value
    private void compute(Map<BlockPrototype.Property<?>, Comparable<?>> map, BlockPrototype.Property<?>[] props, int index,
                         List<ImmutableMap<BlockPrototype.Property<?>, Comparable<?>>> ls) {
        if (index < props.length) {
            BlockPrototype.Property<?> prop = props[index];
            for (Comparable<?> v : prop.getValues()) {
                map.put(prop, v);
                compute(map, props, index + 1, ls);
                map.remove(prop);
            }
        } else {
            ls.add(ImmutableMap.copyOf(map));
        }
    }

    public Block build() {
        AABBd[] boxes = noCollision ? new AABBd[0] : aabBds.size() == 0 ? new AABBd[]{boundingBox} : aabBds.toArray(new AABBd[aabBds.size()]);
        return new BlockShared(boxes, placeBehavior, activeBehavior, touchBehavior, destroyBehavior, null)
                .localName(path);
    }

    public List<Block> buildAll() {
        // if (this.map != null)
        // return Lists.newArrayList(new BlockRuntime(block, placeBehavior,
        // activeBehavior, touchBehavior, destroyBehavior, map));
        BlockPrototype.Property<?>[] props = this.properties.toArray(new BlockPrototype.Property[this.properties.size()]);
        List<ImmutableMap<BlockPrototype.Property<?>, Comparable<?>>> compute = this.compute(props);
        ImmutableTable.Builder<BlockPrototype.Property<?>, Comparable<?>, BlockShared> builder = ImmutableTable.builder();

        AABBd[] boxes = noCollision ? new AABBd[0] : aabBds.size() == 0 ? new AABBd[]{boundingBox} : aabBds.toArray(new AABBd[aabBds.size()]);

        List<BlockShared> collect = compute.stream().map(m -> {
            BlockShared shared = new BlockShared(boxes, placeBehavior, activeBehavior, touchBehavior, destroyBehavior, m);
            List<Map.Entry<BlockPrototype.Property<?>, Comparable<?>>> entries = new ArrayList<>(m.entrySet());
            entries.sort(Comparator.comparing(a -> a.getKey().getName()));
            StringBuilder postfix = new StringBuilder();
            for (Map.Entry<BlockPrototype.Property<?>, Comparable<?>> entry : entries) {
                String name = entry.getKey().getName();
                String value = entry.getValue().toString();
                postfix.append(".").append(name).append("=").append(value);
            }
            shared.localName(this.path + postfix);
            return shared;
        }).collect(Collectors.toList());

        for (BlockShared shared : collect) {
            ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> properties = shared.getProperties();
            for (Map.Entry<BlockPrototype.Property<?>, Comparable<?>> entry : properties.entrySet()) {
                builder.put(entry.getKey(), entry.getValue(), shared);
            }
        }
        ImmutableTable<BlockPrototype.Property<?>, Comparable<?>, BlockShared> table = builder.build();
        for (BlockShared shared : collect) {
            shared.propertiesTable = table;
        }
        return (List<Block>) (Object) collect;
    }
}