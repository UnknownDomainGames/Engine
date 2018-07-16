package unknowndomain.engine.api.resource;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import unknowndomain.engine.api.util.DomainedPath;

public abstract class ResourceBase implements Resource {
	
	private final DomainedPath path;
	
	public ResourceBase(@Nonnull DomainedPath path) {
		this.path = Validate.notNull(path);
	}

	@Override
	public DomainedPath getPath() {
		return path;
	}
}
