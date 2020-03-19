package engine.mod;

import java.util.List;

public class DependencyCheckResult {
    private final boolean passed;
    private final List<Dependency> missing;

    public DependencyCheckResult(List<Dependency> missing) {
        this.passed = missing.isEmpty();
        this.missing = missing;
    }

    public boolean isPassed() {
        return passed;
    }

    public List<Dependency> getMissing() {
        return missing;
    }
}
