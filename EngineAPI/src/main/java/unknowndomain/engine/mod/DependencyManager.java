package unknowndomain.engine.mod;

import java.util.List;

public interface DependencyManager {

    CheckResult checkDependencies(List<ModDependencyEntry> dependencies);

    class CheckResult {
        private final boolean passed;
        private final List<ModDependencyEntry> missing;

        public CheckResult(List<ModDependencyEntry> missing) {
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
}
