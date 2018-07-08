package unknowndomain.engine.api.resource;

import java.util.Collection;

public interface ResourceUpdateListener<T extends Resource> extends ResourceListener<T>{
	/**
	 * get the paths which you want to listen
	 * @return paths
	 */
	public Collection<String> getListenPaths();
	@Override
	public default void update(T resource) {
		if (this.getListenPaths().contains(resource.getURL().getPath())) {
			this.updateResource(resource);
		}
	}
	/**
	 * call when resource update
	 * @param resource
	 */
	public void updateResource(T resource);
}
