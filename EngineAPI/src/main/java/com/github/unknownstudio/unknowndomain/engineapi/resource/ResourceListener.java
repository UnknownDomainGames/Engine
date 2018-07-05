package com.github.unknownstudio.unknowndomain.engineapi.resource;

public interface ResourceListener<T extends Resource> {
	/**
	 * Update when ResourceManager update
	 */
	public void update(T resource);
}
