package unknowndomain.engine.api.event;

import unknowndomain.engine.event.Listener;

import static org.junit.Assert.*;

public class TestParentChildListener {

	@Listener(order = Order.LAST)
	public void onHandle(ExampleChildEvent event) {
		assertEquals(event.value, "Hello! Parent!");
	}
	
	@Listener
	public void onHandle(ExampleParentEvent event) {
		((ExampleChildEvent)event).value = "Hello! Parent!";
	}
}
