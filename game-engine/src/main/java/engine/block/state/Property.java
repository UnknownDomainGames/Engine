package engine.block.state;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class Property<T extends Comparable<T>> {
    private Class<T> clazz;
    private String propertyName;

    public Property(Class<T> clazz, String propertyName) {
        this.clazz = clazz;
        this.propertyName = propertyName;
    }

    public Class<T> getType() {
        return clazz;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public abstract Collection<T> getPossibleValues();

    public abstract String valueToString(T value);

    public abstract Optional<T> stringToValue(String s);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        Property<?> property = (Property<?>) o;
        return Objects.equals(clazz, property.clazz) &&
                Objects.equals(propertyName, property.propertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, propertyName);
    }

    public static class PropertyValue<T extends Comparable<T>> {
        private final Property<T> property;
        private final T value;

        public PropertyValue(Property<T> property, T value) {
            if (!property.getPossibleValues().contains(value)) {
                throw new IllegalArgumentException("Property " + property.getPropertyName() + " does not contains " + value);
            }
            this.property = property;
            this.value = value;
        }

        public Property<T> getProperty() {
            return property;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return property.getPropertyName() + ":" + property.valueToString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PropertyValue)) return false;
            PropertyValue<?> that = (PropertyValue<?>) o;
            return property.equals(that.property) &&
                    value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(property, value);
        }
    }
}
