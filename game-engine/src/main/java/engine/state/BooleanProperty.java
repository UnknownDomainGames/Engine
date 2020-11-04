package engine.state;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Optional;

public class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(String propertyName) {
        super(Boolean.class, propertyName);
    }

    @Override
    public Collection<Boolean> getPossibleValues() {
        return ImmutableSet.of(false, true);
    }

    @Override
    public String valueToString(Boolean value) {
        return value.toString();
    }

    @Override
    public Optional<Boolean> stringToValue(String s) {
        return Optional.ofNullable("true".equals(s) || "false".equals(s) ? Boolean.valueOf(s) : null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BooleanProperty)) return false;
        return super.equals(o);
    }
}
