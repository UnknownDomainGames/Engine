package engine.gui.event;

public interface EventTarget {

    EventDispatchChain buildEventDispatchChain(EventDispatchChain tail);
}
