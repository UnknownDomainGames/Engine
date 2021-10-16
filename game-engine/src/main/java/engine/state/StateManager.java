package engine.state;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StateManager<O, S extends State<O, S>> {
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final List<S> states;

    protected StateManager(O owner, Map<String, Property<?>> properties, BiFunction<O, Map<Property, Comparable>, S> stateSupplier) {
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

        var possibleStatesMap = new HashMap<Map<Property, Comparable>, State>();
        var possibleStatesList = new ArrayList<S>();

        possibleStatesStream.forEach(list -> {
            var map = list.stream().collect(Collectors.toMap(Property.PropertyValue::getProperty, Property.PropertyValue::getValue));
            var state = stateSupplier.apply(owner, map);
            possibleStatesMap.put(map, state);
            possibleStatesList.add(state);
        });

        for (State state : possibleStatesList) {
            state.createSibling(possibleStatesMap);
        }

        states = List.copyOf(possibleStatesList);
    }

    public O getOwner() {
        return owner;
    }

    public List<S> getStates() {
        return states;
    }

    public S getDefaultState() {
        return states.get(0);
    }

    public Collection<Property<?>> getProperties() {
        return properties.values();
    }

    public static class Builder<O, S extends State<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> properties = new HashMap<>();

        public Builder(O owner) {
            this.owner = owner;
        }

        public Builder<O, S> add(Property<?>... properties) {
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

        public StateManager<O, S> build(BiFunction<O, Map<Property, Comparable>, S> stateSupplier) {
            return new StateManager<>(owner, properties, stateSupplier);
        }
    }
}
