package unknowndomain.engine.api.event;

import unknowndomain.engine.event.Event;

public class ExampleParentEvent implements Event {
	
	public String value;

	public ExampleParentEvent(String value) {
		this.value = value;
	}
}
