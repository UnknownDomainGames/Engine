package unknowndomain.engine.event;

import org.junit.jupiter.api.*;
public class TestCancellableListener {

    @Listener(order = Order.FIRST)
    public void onHandle(ExampleCancellableEvent event) {
        event.setCancelled(true);
    }

    @Listener
    public void onHandle0(ExampleCancellableEvent event) {
        throw new RuntimeException("Event has been cancelled.");
    }

    @Listener(receiveCancelled = true)
    public void onHandle1(ExampleCancellableEvent event) {
        Assertions.assertTrue(event.isCancelled());
    }
}
