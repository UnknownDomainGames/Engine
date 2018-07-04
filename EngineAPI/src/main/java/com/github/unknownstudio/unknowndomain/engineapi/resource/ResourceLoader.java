package com.github.unknownstudio.unknowndomain.engineapi.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class ResourceLoader {

	/**
	 * 
	 * @param url
	 * @return stream
	 * @throws IOException 
	 */
	public abstract InputStream getResourceStream(URL url) throws IOException;
	


}
