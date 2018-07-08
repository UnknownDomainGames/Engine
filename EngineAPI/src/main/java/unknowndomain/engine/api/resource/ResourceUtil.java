package unknowndomain.engine.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ResourceUtil {
	private ResourceUtil() {}
	/**
	 * get resource InputStream
	 * @param url
	 * @return stream
	 * @throws IOException
	 */
	public static InputStream getResourceInputStream(URL url) throws IOException {
		return url.openConnection().getInputStream();
	}
	
}
