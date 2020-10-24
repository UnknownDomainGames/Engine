package engine.block.state;

import com.google.common.collect.ImmutableMap;
import engine.block.Block;

import java.util.*;

/**
 * State
 * <p>
 * represents a finite set of variants of a unit
 * <p>
 * Given the finite property of <code>State</code>, all properties of the unit have to be exhaustive,
 * i.e. all possibility of properties can be listed
 * <p>
 * In design, only those which share the same or nearly the same behaviours should use <code>State</code>.
 * Should unit that have distinct behaviours use separate classes and/or registry units.
 * <p>
 * Example 1: Stone Block A and Stone Block B only have visual difference. They could use <code>State</code>
 * <p>
 * Example 2: Stone Block A1 has behaviour 1 and Stone Block A2 has behaviour 2. They should be designed as 2 distinct Block.
 */
public class BlockState {
    protected Block owner;

    private final HashMap<Property<?>, Comparable<?>> properties;

    private Map<Property.PropertyValue<?>, BlockState> sibling;

    public BlockState(Block owner, Map<Property, Comparable> properties) {
        this.owner = owner;
        this.properties = new HashMap<>(properties);
    }

    public Collection<Property<?>> getProperties() {
        return Collections.unmodifiableCollection(properties.keySet());
    }

    public ImmutableMap<Property<?>, Comparable<?>> getPropertiesWithValue() {
        return ImmutableMap.copyOf(properties);
    }

    public boolean contains(Property<?> property) {
        return this.properties.containsKey(property);
    }

    public <T extends Comparable<T>> T get(Property<T> property) {
        if (!contains(property)) {
            throw new IllegalArgumentException(owner + " does not contains property " + property);
        }
        return property.getType().cast(properties.get(property));
    }

    public <T extends Comparable<T>> Optional<T> getOptional(Property<T> property) {
        Comparable<?> comparable = properties.get(property);
        return comparable == null ? Optional.empty() : Optional.of(property.getType().cast(comparable));
    }

    public <T extends Comparable<T>> BlockState with(Property<T> property, T value) {
        if (!properties.containsKey(property)) {
            throw new IllegalArgumentException(this.owner + " does not have property " + property);
        }
        Comparable<?> comparable = properties.get(property);
        if (comparable == value) {
            return this;
        }
        var propertyValue = new Property.PropertyValue<T>(property, value);
        var state = sibling.get(propertyValue);
        if (state == null) {
            throw new IllegalArgumentException(propertyValue + " is not allowed in " + owner);
        }
        return state;
    }

    public void createSibling(HashMap<Map<Property, Comparable>, BlockState> possibleStatesMap) {
        if (sibling != null) {
            throw new IllegalStateException("Sibling map already created");
        }
        sibling = new HashMap<>();
        for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
            for (Comparable<?> possibleValue : entry.getKey().getPossibleValues()) {
                if (entry.getValue() != possibleValue) {
                    var k = new HashMap<>(properties);
                    k.put(entry.getKey(), possibleValue);
                    sibling.put(new Property.PropertyValue(entry.getKey(), possibleValue), possibleStatesMap.get(k));
                }
            }
        }
    }
}
