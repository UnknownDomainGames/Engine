package nullengine.mod;

public enum DependencyType {
    /**
     * Mod must be loaded, and after it loadDirect your mod.
     */
    REQUIRED,
    /**
     * Mod needn't be loaded, but after it loadDirect your mod.
     */
    AFTER
}
