package nullengine.mod;

import java.util.List;

public interface DependencyManager {

    DependencyCheckResult checkDependencies(List<ModDependencyEntry> dependencies);
}
