package unknowndomain.engine.mod.exception;

import unknowndomain.engine.mod.DependencyManager;

public class MissingDependencyException extends RuntimeException {

    private final String modId;
    private final DependencyManager.CheckResult result;

    public MissingDependencyException(String modId, DependencyManager.CheckResult result) {
        super(String.format("Missing dependency when load mod %s.", modId));
        this.modId = modId;
        this.result = result;
    }

    public String getModId() {
        return modId;
    }

    public DependencyManager.CheckResult getResult() {
        return result;
    }
}
