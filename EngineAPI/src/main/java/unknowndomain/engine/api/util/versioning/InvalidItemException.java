package unknowndomain.engine.api.util.versioning;

public class InvalidItemException extends Exception {
	public InvalidItemException(Class<?> clazz){
		super("invalid item: " + clazz);
	}
}
