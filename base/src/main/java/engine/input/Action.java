package engine.input;

public enum Action {
    RELEASE, PRESS, REPEAT;

    private static final Action[] VALUES = values();

    public static Action valueOf(int action) {
        return VALUES[action];
    }
}
