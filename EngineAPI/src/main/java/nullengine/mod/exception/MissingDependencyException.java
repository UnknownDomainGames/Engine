package nullengine.mod.exception;

import nullengine.mod.DependencyCheckResult;

public class MissingDependencyException extends RuntimeException {

    private final String modId;
    private final DependencyCheckResult result;

    public MissingDependencyException(String modId, DependencyCheckResult result) {
        super(String.format("Missing dependency when loadDirect mod %s.", modId));
        this.modId = modId;
        this.result = result;
    }

    public String getModId() {
        return modId;
    }

    public DependencyCheckResult getResult() {
        return result;
    }
}
