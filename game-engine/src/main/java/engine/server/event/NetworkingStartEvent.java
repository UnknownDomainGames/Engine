package engine.server.event;

import engine.event.Event;
import engine.event.EventBus;

public class NetworkingStartEvent implements Event {
    private EventBus networkBus;

    public NetworkingStartEvent(EventBus eventBus) {
        this.networkBus = eventBus;
    }

    public EventBus getNetworkingEventBus() {
        return networkBus;
    }
}
