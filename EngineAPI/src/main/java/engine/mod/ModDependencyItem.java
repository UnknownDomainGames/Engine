package engine.mod;

import engine.mod.exception.InvalidDependencyException;
import engine.util.versioning.InvalidVersionSpecificationException;
import engine.util.versioning.VersionRange;

public class ModDependencyItem {

    public static ModDependencyItem parse(String spec) {
        String[] args = spec.split(":");
        if (args.length != 3) {
            throw new InvalidDependencyException("Failed to parse dependency entry. Source: " + spec);
        }

        try {
            String id = args[0];
            VersionRange versionRange = VersionRange.createFromVersionSpec(args[1]);
            DependencyType type = DependencyType.valueOf(args[2].toUpperCase());
            return new ModDependencyItem(id, versionRange, type);
        } catch (InvalidVersionSpecificationException e) {
            throw new InvalidDependencyException(String.format("Failed to parse dependency entry, invalid version range \"%s\".", args[1]), e);
        } catch (IllegalArgumentException e) {
            throw new InvalidDependencyException("Failed to parse dependency entry, illegal dependency type. Type: " + args[2], e);
        }
    }

    private final String id;
    private final VersionRange versionRange;
    private final DependencyType type;

    public ModDependencyItem(String id, String versionRange, DependencyType type) {
        this(id, VersionRange.createFromVersionSpec(versionRange), type);
    }

    public ModDependencyItem(String id, VersionRange versionRange, DependencyType type) {
        this.id = id;
        this.versionRange = versionRange;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public VersionRange getVersionRange() {
        return versionRange;
    }

    public DependencyType getType() {
        return type;
    }

    @Override
    public String toString() {
        return id + ":" + versionRange + ":" + type;
    }
}
