package engine.client.asset.reloading;

import engine.util.KeyComparable;

import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

public final class AssetReloadHandler implements KeyComparable<String, AssetReloadHandler> {

    private final String name;
    private final Runnable runnable;
    private final Set<String> before;
    private final Set<String> after;

    public static Builder builder() {
        return new Builder();
    }

    private AssetReloadHandler(String name, Runnable runnable, Set<String> before, Set<String> after) {
        this.name = notNull(name);
        this.runnable = notNull(runnable);
        this.before = before;
        this.after = after;
    }

    public String getName() {
        return name;
    }

    public void onReload() {
        runnable.run();
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

    public static final class Builder {
        private String name;
        private Runnable runnable;
        private Set<String> before = Set.of();
        private Set<String> after = Set.of();

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder runnable(Runnable runnable) {
            this.runnable = runnable;
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

        public AssetReloadHandler build() {
            return new AssetReloadHandler(name, runnable, before, after);
        }
    }
}
