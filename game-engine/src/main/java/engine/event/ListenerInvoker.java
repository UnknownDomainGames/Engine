package engine.event;

interface ListenerInvoker {
    void invoke(Event event) throws Throwable;
}
