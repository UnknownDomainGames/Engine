package unknowndomain.engine.mod;

import java.util.List;

public interface DependencyManager {

    DependencyCheckResult checkDependencies(List<ModDependencyEntry> dependencies);
}
