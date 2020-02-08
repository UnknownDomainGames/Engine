package engine.gui.event;

public interface EventDispatchChain {

    EventDispatchChain append(EventDispatcher eventDispatcher);

    EventDispatchChain prepend(EventDispatcher eventDispatcher);

    Event dispatchEvent(Event event);
}
