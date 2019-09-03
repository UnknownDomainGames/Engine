package nullengine.client.asset.reloading;

import java.util.Set;

public final class AssetReloadListener implements Comparable<AssetReloadListener> {

    private String name;
    private Runnable runnable;
    private Set<String> befores = Set.of();
    private Set<String> afters = Set.of();

    public AssetReloadListener name(String name) {
        this.name = name;
        return this;
    }

    public AssetReloadListener runnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public AssetReloadListener befores(String... befores) {
        this.befores = Set.of(befores);
        return this;
    }

    public AssetReloadListener afters(String... afters) {
        this.afters = Set.of(afters);
        return this;
    }

    public String getName() {
        return name;
    }

    public void onReload() {
        runnable.run();
    }

    @Override
    public int compareTo(AssetReloadListener o) {
        if (afters.contains(o.name) || o.befores.contains(name))
            return 1;

        if (befores.contains(o.name) || o.afters.contains(name))
            return -1;

        return 0;
    }


}
