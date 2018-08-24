package unknowndomain.engine;

public interface Tickable {
    void tick();

    interface Partial {
        void tick(double partial);
    }
}
