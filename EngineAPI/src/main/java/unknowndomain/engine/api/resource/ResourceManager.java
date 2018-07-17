package unknowndomain.engine.api.resource;

import unknowndomain.engine.api.util.DomainedPath;

public interface ResourceManager {

	boolean exists(DomainedPath path);

	boolean isFile(DomainedPath path);
}
