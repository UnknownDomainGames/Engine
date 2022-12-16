package engine.event;

public interface EventExceptionHandler {
    EventExceptionHandler IGNORE = new EventExceptionHandler() {
        @Override
        public void handle(Event event, Throwable throwable) {
            // Nothing to do.
        }
    };
    EventExceptionHandler RETHROW = new EventExceptionHandler() {
        @Override
        public void handle(Event event, Throwable throwable) {
            throw new EventException("Cannot handle event: " + event.getClass().getName(), throwable);
        }
    };
    EventExceptionHandler PRINT = new EventExceptionHandler() {
        @Override
        public void handle(Event event, Throwable throwable) {
            throwable.printStackTrace();
        }
    };

    void handle(Event event, Throwable throwable);
}
