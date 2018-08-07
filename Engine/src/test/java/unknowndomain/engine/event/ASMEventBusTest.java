package unknowndomain.engine.event;

import org.junit.jupiter.api.*;

public class ASMEventBusTest {

    private static EventBus eventBus;

    @BeforeAll
    public static void setup() {
        eventBus = new AsmEventBus();
        eventBus.register(new TestOrderListener());
        eventBus.register(new TestCancellableListener());
        eventBus.register(new TestParentChildListener());
    }

    @Test
    public void testOrder() {
        Assertions.assertFalse(eventBus.post(new ExampleEvent("Hello! AsmEventBus!")));
    }

    @Test
    public void testCancellable() {
        Assertions.assertTrue(eventBus.post(new ExampleCancellableEvent()));
    }

    @Test
    public void testParent() {
        Assertions.assertFalse(eventBus.post(new ExampleChildEvent("Hello! AsmEventBus!")));
    }
}
