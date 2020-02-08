package engine.world.hit;

public abstract class HitResult {

    private final Type type;

    public HitResult(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isSuccess() {
        return type != Type.MISS;
    }

    public boolean isFailure() {
        return type == Type.MISS;
    }

    public void ifFailure(Runnable runnable) {
        if (isFailure()) {
            runnable.run();
        }
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY
    }
}
