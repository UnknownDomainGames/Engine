package unknowndomain.engine.event;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EventBusTest {

    private static EventBus eventBus;

    @BeforeClass
    public static void setup() {
        eventBus = new AsmEventBus();
        eventBus.register(new TestOrderListener());
        eventBus.register(new TestCancellableListener());
        eventBus.register(new TestParentChildListener());
    }

    @Test
    public void testOrder() {
        Assert.assertFalse(eventBus.post(new ExampleEvent("Hello! AsmEventBus!")));
    }

    @Test
    public void testCancellable() {
        Assert.assertTrue(eventBus.post(new ExampleCancellableEvent()));
    }

    @Test
    public void testParent() {
        Assert.assertFalse(eventBus.post(new ExampleChildEvent("Hello! AsmEventBus!")));
    }
}
