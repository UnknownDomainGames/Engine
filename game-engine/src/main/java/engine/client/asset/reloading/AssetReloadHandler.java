package engine.client.asset.reloading;

import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

public final class AssetReloadHandler implements Comparable<AssetReloadHandler> {

    private final String name;
    private final Runnable runnable;
    private final Set<String> beforeNodes;
    private final Set<String> afterNodes;

    public static Builder builder() {
        return new Builder();
    }

    private AssetReloadHandler(Builder builder) {
        this.name = notNull(builder.name);
        this.runnable = notNull(builder.runnable);
        this.beforeNodes = builder.beforeNodes;
        this.afterNodes = builder.afterNodes;
    }

    public void doReload() {
        runnable.run();
    }

    public String getName() {
        return name;
    }

    public Set<String> beforeNodes() {
        return beforeNodes;
    }

    public Set<String> afterNodes() {
        return afterNodes;
    }

    @Override
    public int compareTo(AssetReloadHandler o) {
        if (afterNodes().contains(o.getName()) || o.beforeNodes().contains(getName()))
            return 1;

        if (beforeNodes().contains(o.getName()) || o.afterNodes().contains(getName()))
            return -1;

        return 0;
    }

    public static final class Builder {
        private String name;
        private Runnable runnable;
        private Set<String> beforeNodes = Set.of();
        private Set<String> afterNodes = Set.of();

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

        public Builder before(String... beforeNodes) {
            this.beforeNodes = Set.of(beforeNodes);
            return this;
        }

        public Builder after(String... afterNodes) {
            this.afterNodes = Set.of(afterNodes);
            return this;
        }

        public AssetReloadHandler build() {
            return new AssetReloadHandler(this);
        }
    }
}
