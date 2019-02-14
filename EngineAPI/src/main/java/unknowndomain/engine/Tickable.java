package unknowndomain.engine;

// TODO: move it
public interface Tickable {
    void tick();

    interface Partial {
        void tick(float partial);
    }
}
