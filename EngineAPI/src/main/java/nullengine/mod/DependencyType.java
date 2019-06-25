package nullengine.mod;

public enum DependencyType {
    /**
     * Mod must be loaded, and after it load your mod.
     */
    REQUIRED,
    /**
     * Mod needn't be loaded, but after it load your mod.
     */
    AFTER
}
