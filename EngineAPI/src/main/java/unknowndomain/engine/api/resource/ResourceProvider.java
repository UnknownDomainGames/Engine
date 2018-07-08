package unknowndomain.engine.api.resource;

import java.net.URL;
import java.util.Collection;

public interface ResourceProvider {
	/**
	 * Register Manager
	 * @param manager
	 */
	public void registerManager(ResourceManager<? extends Resource> manager);
	/**
	 * load set of resources
	 * @param url
	 */
	public void loadResources(Collection<URL> url);
	/**
	 * 
	 * @param url
	 */
	public void loadResource(URL url);
	/**
	 * get priority
	 * @return priority
	 */
	public ResourcePriority getPriority();
}
