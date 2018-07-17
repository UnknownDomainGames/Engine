package unknowndomain.engine.api.resource;

import java.io.InputStream;
import java.net.URL;

import unknowndomain.engine.api.util.DomainedPath;

public interface ResourceSource {
	
	InputStream openStream(DomainedPath path);
	
	boolean exists(DomainedPath path);

	boolean isFile(DomainedPath path);
	
	URL toURL(DomainedPath path);
}
