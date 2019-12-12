package nullengine.client.gui.event;

public class EventUtils {

    private static final ThreadLocal<EventDispatchChainImpl> eventDispatchChain = ThreadLocal.withInitial(EventDispatchChainImpl::new);

    public static Event fireEvent(Event event, EventTarget eventTarget) {
        EventDispatchChainImpl eventDispatchChain = EventUtils.eventDispatchChain.get();
        eventDispatchChain.reset();
        return eventTarget.buildEventDispatchChain(eventDispatchChain).dispatchEvent(event);
    }
}
