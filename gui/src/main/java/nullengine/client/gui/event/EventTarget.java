package nullengine.client.gui.event;

public interface EventTarget {

    EventDispatchChain buildEventDispatchChain(EventDispatchChain tail);
}
