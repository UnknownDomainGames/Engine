package engine.server.event;

import engine.event.Event;

public class NetworkDisconnectedEvent implements Event {
    private String reason;

    public NetworkDisconnectedEvent(String reason){
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
