package engine.server.event;

import engine.event.Event;
import engine.server.network.NetworkHandler;

public class NetworkDisconnectedEvent implements Event {

    private NetworkHandler handler;

    private String reason;

    public NetworkDisconnectedEvent(NetworkHandler handler, String reason) {
        this.handler = handler;
        this.reason = reason;
    }

    public NetworkHandler getHandler() {
        return handler;
    }

    public String getReason() {
        return reason;
    }
}
