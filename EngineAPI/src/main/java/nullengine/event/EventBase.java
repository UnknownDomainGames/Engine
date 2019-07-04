package nullengine.event;

import java.util.Optional;

public class EventBase implements Event {
    private Event parent;

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public Optional<Event> getCausality() {
        return Optional.ofNullable(parent);
    }

    @Override
    public void setCausality(Event event) {
        this.parent = event;
    }

    public static class Cancellable extends EventBase implements nullengine.event.Cancellable {
        private boolean cancelled = false;

        public Cancellable() {
            super();
        }

        @Override
        public boolean isCancellable() {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
