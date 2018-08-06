package unknowndomain.engine.api.event;

import unknowndomain.engine.event.Event;

public class ExampleEvent implements Event {
	
	public String value;

	public ExampleEvent(String value) {
		this.value = value;
	}
}
