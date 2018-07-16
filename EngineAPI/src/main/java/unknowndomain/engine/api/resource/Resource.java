package unknowndomain.engine.api.resource;

import java.net.URL;
import java.nio.file.Path;

public interface Resource {
	/**
	 * 
	 * @return content whitch you want to write
	 */
	public byte[] getContent();
	/**
	 * 
	 * @return resource path
	 */
	public Path getPath();
}
