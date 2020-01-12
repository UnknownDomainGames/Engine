package nullengine.client.gui.event;

public class EventUtils {

    public static Event fireEvent(Event event, EventTarget eventTarget) {
        EventDispatchChainImpl eventDispatchChain = new EventDispatchChainImpl();
        return eventTarget.buildEventDispatchChain(eventDispatchChain).dispatchEvent(event);
    }
}
