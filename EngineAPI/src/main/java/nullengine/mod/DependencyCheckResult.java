package nullengine.mod;

import java.util.List;

public class DependencyCheckResult {
    private final boolean passed;
    private final List<ModDependencyItem> missing;

    public DependencyCheckResult(List<ModDependencyItem> missing) {
        this.passed = missing.isEmpty();
        this.missing = missing;
    }

    public boolean isPassed() {
        return passed;
    }

    public List<ModDependencyItem> getMissing() {
        return missing;
    }
}
