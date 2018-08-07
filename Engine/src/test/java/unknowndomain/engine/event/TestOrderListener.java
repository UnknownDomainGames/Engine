package unknowndomain.engine.event;

import org.junit.jupiter.api.*;
public class TestOrderListener {

    private int count;

    @Listener(order = Order.LAST)
    public void onHandleLast(ExampleEvent event) {
        Assertions.assertEquals(count, 5);
    }

    @Listener(order = Order.LATE)
    public void onHandleLate(ExampleEvent event) {
        Assertions.assertEquals(count, 4);
        count++;
    }

    @Listener
    public void onHandleDefault(ExampleEvent event) {
        Assertions.assertEquals(count, 3);
        count++;
    }

    @Listener(order = Order.EARLY)
    public void onHandleEarly(ExampleEvent event) {
        Assertions.assertEquals(count, 2);
        count++;
    }

    @Listener(order = Order.FIRST)
    public void onHandleFirst(ExampleEvent event) {
        count = 1;
        Assertions.assertEquals(event.value, "Hello! AsmEventBus!");
        Assertions.assertEquals(count, 1);
        count++;
    }
}
