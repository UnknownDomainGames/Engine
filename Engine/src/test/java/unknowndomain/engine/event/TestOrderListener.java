package unknowndomain.engine.event;

import org.junit.Assert;

public class TestOrderListener {

    private int count;

    @Listener(order = Order.LAST)
    public void onHandleLast(ExampleEvent event) {
        Assert.assertEquals(count, 5);
    }

    @Listener(order = Order.LATE)
    public void onHandleLate(ExampleEvent event) {
        Assert.assertEquals(count, 4);
        count++;
    }

    @Listener
    public void onHandleDefault(ExampleEvent event) {
        Assert.assertEquals(count, 3);
        count++;
    }

    @Listener(order = Order.EARLY)
    public void onHandleEarly(ExampleEvent event) {
        Assert.assertEquals(count, 2);
        count++;
    }

    @Listener(order = Order.FIRST)
    public void onHandleFirst(ExampleEvent event) {
        count = 1;
        Assert.assertEquals(event.value, "Hello! AsmEventBus!");
        Assert.assertEquals(count, 1);
        count++;
    }
}
