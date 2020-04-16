package engine.gui.event;

import java.util.concurrent.atomic.AtomicBoolean;

public final class EventUtils {

    private static final EventDispatchChainImpl EVENT_DISPATCH_CHAIN = new EventDispatchChainImpl();
    private static final AtomicBoolean EVENT_DISPATCH_CHAIN_IN_USE = new AtomicBoolean();

    public static Event fireEvent(Event event, EventTarget eventTarget) {
        if (EVENT_DISPATCH_CHAIN_IN_USE.getAndSet(true)) {
            return eventTarget.buildEventDispatchChain(new EventDispatchChainImpl()).dispatchEvent(event);
        }

        try {
            return eventTarget.buildEventDispatchChain(EVENT_DISPATCH_CHAIN).dispatchEvent(event);
        } finally {
            EVENT_DISPATCH_CHAIN.reset();
            EVENT_DISPATCH_CHAIN_IN_USE.set(false);
        }
    }

    private EventUtils() {
    }
}
