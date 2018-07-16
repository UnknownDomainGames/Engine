package unknowndomain.engine.api.resource;


import java.util.List;

public interface ResourceManager<T extends Resource> {
	/**
	 * 
	 * @param url
	 * @return resource
	 */
	T getResource(String path);
	/**
	 * 
	 * @param resource
	 */
	void updateResource(T resource);

	List<T> getResources();
}
