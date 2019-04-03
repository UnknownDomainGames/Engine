package unknowndomain.engine.logic;

// TODO: move it
public interface Tickable {
    void tick();

    interface Partial {
        void tick(float partial);
    }
}
