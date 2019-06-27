package nullengine.mod.impl;

import nullengine.mod.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nullengine.mod.DependencyType.REQUIRED;

public class DependencyManagerImpl implements DependencyManager {

    private final ModManager modManager;

    public DependencyManagerImpl(ModManager modManager) {
        this.modManager = modManager;
    }

    @Override
    public DependencyCheckResult checkDependencies(List<ModDependencyEntry> dependencies) {
        List<ModDependencyEntry> missing = new ArrayList<>();

        for (ModDependencyEntry dependency : dependencies) {
            if (dependency.getType() != REQUIRED) {
                continue;
            }

            Optional<ModContainer> mod = modManager.getMod(dependency.getId());
            if (mod.isEmpty() || !dependency.getVersionRange().containsVersion(mod.get().getMetadata().getVersion())) {
                missing.add(dependency);
            }
        }

        return new DependencyCheckResult(List.copyOf(missing));
    }

    @Override
    public List<ModContainer> getDependencies(List<ModDependencyEntry> dependencies) {
        List<ModContainer> mods = new ArrayList<>();

        for (ModDependencyEntry dependency : dependencies) {
            modManager.getMod(dependency.getId()).ifPresent(mods::add);
        }

        return mods;
    }
}
