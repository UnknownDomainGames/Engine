package com.github.unknownstudio.unknowndomain.engineapi.resource;


public interface ResourceManager<T extends Resource> {
	/**
	 * 
	 * @param url
	 * @return resource
	 */
	public T getResource(String path);
	/**
	 * 
	 * @param resource
	 */
	public void updateResource(T resource);
}
