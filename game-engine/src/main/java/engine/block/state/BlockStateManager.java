package engine.block.state;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import engine.block.Block;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockStateManager {
    private final Block owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final List<BlockState> states;

    protected BlockStateManager(Block owner, Map<String, Property<?>> properties) {
        this.owner = owner;
        this.properties = ImmutableSortedMap.copyOf(properties);

        var possibleStatesStream = Stream.of(Collections.<Property.PropertyValue>emptyList());

        //Listing all possible property values
        for (Property<?> value : this.properties.values()) {
            possibleStatesStream = possibleStatesStream.flatMap(ps -> value.getPossibleValues().stream().map(comparable -> {
                var list = Lists.newArrayList(ps);
                list.add(new Property.PropertyValue(value, comparable));
                return list;
            }));
        }

        var possibleStatesMap = new HashMap<Map<Property, Comparable>, BlockState>();
        var possibleStatesList = new ArrayList<BlockState>();

        possibleStatesStream.forEach(list -> {
            var map = list.stream().collect(Collectors.toMap(Property.PropertyValue::getProperty, Property.PropertyValue::getValue));
            var state = new BlockState(owner, map);
            possibleStatesMap.put(map, state);
            possibleStatesList.add(state);
        });

        for (BlockState state : possibleStatesList) {
            state.createSibling(possibleStatesMap);
        }

        states = List.copyOf(possibleStatesList);
    }

    public Block getOwner() {
        return owner;
    }

    public List<BlockState> getStates() {
        return states;
    }

    public BlockState getDefaultState() {
        return states.get(0);
    }

    public Collection<Property<?>> getProperties() {
        return properties.values();
    }

    public static class Builder {
        private final Block owner;
        private final Map<String, Property<?>> properties = new HashMap<>();

        public Builder(Block owner) {
            this.owner = owner;
        }

        public Builder add(Property<?>... properties) {
            for (Property<?> property : properties) {
                validate(property);
                this.properties.put(property.getPropertyName(), property);
            }
            return this;
        }

        private <T extends Comparable<T>> void validate(Property<T> property) {
            if (properties.containsKey(property.getPropertyName())) {
                throw new IllegalArgumentException("Property " + property.getPropertyName() + " already exists in " + owner);
            }
        }

        public BlockStateManager build() {
            return new BlockStateManager(owner, properties);
        }
    }
}
