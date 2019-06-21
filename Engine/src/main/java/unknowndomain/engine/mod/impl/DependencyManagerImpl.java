package unknowndomain.engine.mod.impl;

import unknowndomain.engine.mod.DependencyManager;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDependencyEntry;
import unknowndomain.engine.mod.ModManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static unknowndomain.engine.mod.DependencyType.REQUIRED;

public class DependencyManagerImpl implements DependencyManager {

    private final ModManager modManager;

    public DependencyManagerImpl(ModManager modManager) {
        this.modManager = modManager;
    }

    @Override
    public CheckResult checkDependencies(List<ModDependencyEntry> dependencies) {
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

        return new CheckResult(List.copyOf(missing));
    }
}
