package unknowndomain.engine.event;

import org.junit.Assert;

public class TestParentChildListener {

    @Listener(order = Order.LAST)
    public void onHandle(ExampleChildEvent event) {
        Assert.assertEquals(event.value, "Hello! Parent!");
    }

    @Listener
    public void onHandle(ExampleParentEvent event) {
        ((ExampleChildEvent) event).value = "Hello! Parent!";
    }
}
