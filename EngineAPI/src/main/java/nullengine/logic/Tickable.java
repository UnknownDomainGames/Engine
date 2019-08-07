package nullengine.logic;

public interface Tickable {
    void tick();

    interface Partial {
        void tick(float partial);
    }
}
