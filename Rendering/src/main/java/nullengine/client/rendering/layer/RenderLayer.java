package nullengine.client.rendering.layer;

import nullengine.util.KeyComparable;

import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

public final class RenderLayer implements KeyComparable<String, RenderLayer> {

    private final String name;
    private final Set<String> before;
    private final Set<String> after;

    private RenderLayer(String name, Set<String> before, Set<String> after) {
        this.name = notNull(name);
        this.before = before;
        this.after = after;
    }

    public String getName() {
        return name;
    }

    @Override
    public String key() {
        return name;
    }

    @Override
    public Set<String> beforeThis() {
        return before;
    }

    @Override
    public Set<String> afterThis() {
        return after;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private Set<String> before = Set.of();
        private Set<String> after = Set.of();

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder before(String... before) {
            this.before = Set.of(before);
            return this;
        }

        public Builder after(String... after) {
            this.after = Set.of(after);
            return this;
        }

        public RenderLayer build() {
            return new RenderLayer(name, before, after);
        }
    }
}
