package unknowndomain.engine.api.resource;


public interface ResourceManager<T extends Resource> {
	/**
	 * 
	 * @param url
	 * @return resource
	 */
	public T getResource(String path);
	/**
	 * 
	 * @param resource
	 */
	public void updateResource(T resource);
}
