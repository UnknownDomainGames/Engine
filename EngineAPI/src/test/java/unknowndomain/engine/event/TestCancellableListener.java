package unknowndomain.engine.api.event;

import unknowndomain.engine.event.Listener;

import static org.junit.Assert.*;

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
		assertTrue(event.isCancelled());
	}
}
