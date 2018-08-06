package unknowndomain.engine.event;

public interface EventBus {

    static EventBus create() {
        return Internal0.ASM.get();
    }

    /**
     * @param event
     * @return True if cancelled, false if not.
     */
    boolean post(Event event);

    void register(Object listener);

    void unregister(Object listener);
}
