package unknowndomain.engine.api.event;

public class ExampleParentEvent implements Event{
	
	public String value;

	public ExampleParentEvent(String value) {
		this.value = value;
	}
}
