package nullengine.mod.impl;

import nullengine.mod.DependencyCheckResult;
import nullengine.mod.ModContainer;
import nullengine.mod.ModDependencyItem;
import nullengine.mod.ModManager;
import nullengine.util.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static nullengine.mod.DependencyType.REQUIRED;

public class DependencyManager {

    private static final Comparator<ModCandidate> MOD_CANDIDATE_COMPARATOR = (o1, o2) -> {
        for (ModDependencyItem dependency : o1.getMetadata().getDependencies()) {
            if (dependency.getId().equals(o2.getMetadata().getId())) {
                return 1;
            }
        }
        for (ModDependencyItem dependency : o2.getMetadata().getDependencies()) {
            if (dependency.getId().equals(o1.getMetadata().getId())) {
                return -1;
            }
        }
        return 0;
    };

    private final ModManager modManager;

    public DependencyManager(ModManager modManager) {
        this.modManager = modManager;
    }

    public List<ModCandidate> sortModCandidates(List<ModCandidate> modCandidates) {
        return SortedList.copyOf(modCandidates, MOD_CANDIDATE_COMPARATOR);
    }

    public DependencyCheckResult checkDependencies(List<ModDependencyItem> dependencies) {
        List<ModDependencyItem> missing = new ArrayList<>();

        for (ModDependencyItem dependency : dependencies) {
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

    public List<ModContainer> getDependentMods(List<ModDependencyItem> dependencies) {
        List<ModContainer> mods = new ArrayList<>();

        for (ModDependencyItem dependency : dependencies) {
            modManager.getMod(dependency.getId()).ifPresent(mods::add);
        }

        return mods;
    }
}
