package unknowndomain.engine.api.event;

import unknowndomain.engine.event.Listener;

import static org.junit.Assert.assertEquals;

public class TestOrderListener {
	
	private int count;
	
	@Listener(order = Order.LAST)
	public void onHandleLast(ExampleEvent event) {
		assertEquals(count, 5);
	}
	
	@Listener(order = Order.LATE)
	public void onHandleLate(ExampleEvent event) {
		assertEquals(count, 4);
		count++;
	}

	@Listener
	public void onHandleDefault(ExampleEvent event) {
		assertEquals(count, 3);
		count++;
	}
	
	@Listener(order = Order.EARLY)
	public void onHandleEarly(ExampleEvent event) {
		assertEquals(count, 2);
		count++;
	}
	
	@Listener(order = Order.FIRST)
	public void onHandleFirst(ExampleEvent event) {
		count = 1;
		assertEquals(event.value,  "Hello! AsmEventBus!");
		assertEquals(count, 1);
		count++;
	}
}
