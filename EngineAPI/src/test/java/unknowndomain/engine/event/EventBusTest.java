package unknowndomain.engine.api.event;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import unknowndomain.engine.event.AsmEventBus;

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
		assertFalse(eventBus.post(new ExampleEvent("Hello! AsmEventBus!")));
	}
	
	@Test
	public void testCancellable() {
		assertTrue(eventBus.post(new ExampleCancellableEvent()));
	}
	
	@Test
	public void testParent() {
		assertFalse(eventBus.post(new ExampleChildEvent("Hello! AsmEventBus!")));
	}
}
