package unknowndomain.engine.api.resource;

public interface ResourceListener<T extends Resource> {
	/**
	 * Update when ResourceManager update
	 */
	public void update(T resource);
}
