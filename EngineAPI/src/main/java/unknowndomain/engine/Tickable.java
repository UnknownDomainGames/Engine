package unknowndomain.engine;

public interface Tickable {
    void tick();

    interface Partial {
        void tick(double partial);
    }
    interface LogicTicker{
    	void tick(long currentTick);
    }
}
