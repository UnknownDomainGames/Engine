package unknowndomain.engine.mod;

import java.util.List;

public class DependencyCheckResult {
    private final boolean passed;
    private final List<ModDependencyEntry> missing;

    public DependencyCheckResult(List<ModDependencyEntry> missing) {
        this.passed = missing.isEmpty();
        this.missing = missing;
    }

    public boolean isPassed() {
        return passed;
    }

    public List<ModDependencyEntry> getMissing() {
        return missing;
    }
}
