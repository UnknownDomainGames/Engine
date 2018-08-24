package unknowndomain.engine.mod;

import unknowndomain.engine.util.versioning.InvalidVersionSpecificationException;
import unknowndomain.engine.util.versioning.VersionRange;

public class ModDependencyEntry {

    public enum LoadOrder {
        /**
         * Mod must be installed, and after it load your mod.
         */
        REQUIRED,
        /**
         * Mod needn't be installed, but after it load your mod.
         */
        AFTER,
        /**
         * Mod needn't be installed, but before it load your mod.
         */
        BEFORE;
    }

    public static ModDependencyEntry create(String spec) {
        String[] args = spec.split(":", 3);
        if(args.length < 3)
            throw new ModDependencyException("Failed to create dependency entry. Source: " + spec);

        try {
            LoadOrder loadOrder = LoadOrder.valueOf(args[0].toUpperCase());
            String modId = args[1];
            VersionRange range = VersionRange.createFromVersionSpec(args[2]);
            return new ModDependencyEntry(loadOrder, modId, range);
        } catch (InvalidVersionSpecificationException e) {
            throw new ModDependencyException("Failed to create dependency entry, invalid version range. Range: " + args[2], e);
        } catch (IllegalArgumentException e) {
            throw new ModDependencyException("Failed to create dependency entry, illegal load order. Load order: " + args[0], e);
        }
    }

    private final LoadOrder loadOrder;
    private final String modId;
    private final VersionRange versionRange;

    public ModDependencyEntry(LoadOrder loadOrder, String modId, VersionRange versionRange) {
        this.loadOrder = loadOrder;
        this.modId = modId;
        this.versionRange = versionRange;
    }

    public LoadOrder getLoadOrder() {
        return loadOrder;
    }

    public String getModId() {
        return modId;
    }

    public VersionRange getVersionRange() {
        return versionRange;
    }
}
