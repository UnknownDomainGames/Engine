package com.github.unknownstudio.unknowndomain.engineapi.resource;

import java.net.URL;

public interface Resource {
	/**
	 * 
	 * @return content whitch you want to write
	 */
	public byte[] getContent();
	/**
	 * 
	 * @return resource url
	 */
	public URL getURL();
}
