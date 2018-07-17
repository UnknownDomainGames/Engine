package unknowndomain.engine.api.resource;


import java.util.List;

import unknowndomain.engine.api.util.DomainedPath;

public interface ResourceCache<T extends Resource> {

	T getResource(DomainedPath path);

	List<T> getResources();
}
