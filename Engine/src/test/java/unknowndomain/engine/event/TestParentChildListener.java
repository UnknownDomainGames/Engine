package unknowndomain.engine.event;

import org.junit.jupiter.api.*;


public class TestParentChildListener {

    @Listener(order = Order.LAST)
    public void onHandle(ExampleChildEvent event) {
        Assertions.assertEquals(event.value, "Hello! Parent!");
    }

    @Listener
    public void onHandle(ExampleParentEvent event) {
        ((ExampleChildEvent) event).value = "Hello! Parent!";
    }
}
