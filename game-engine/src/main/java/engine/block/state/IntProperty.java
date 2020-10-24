package engine.block.state;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;

public class IntProperty extends Property<Integer> {

    private final ImmutableSet<Integer> availableValues;

    public IntProperty(String propertyName, int min, int max) {
        super(Integer.class, propertyName);
        availableValues = ImmutableSet.<Integer>builder().addAll(IntStream.rangeClosed(min, max).iterator()).build();
    }

    @Override
    public Collection<Integer> getPossibleValues() {
        return availableValues;
    }

    @Override
    public String valueToString(Integer value) {
        return value.toString();
    }

    @Override
    public Optional<Integer> stringToValue(String s) {
        try {
            var value = Integer.valueOf(s);
            return availableValues.contains(value) ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntProperty)) return false;
        if (!super.equals(o)) return false;

        IntProperty that = (IntProperty) o;

        return availableValues.equals(that.availableValues);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + availableValues.hashCode();
        return result;
    }
}
