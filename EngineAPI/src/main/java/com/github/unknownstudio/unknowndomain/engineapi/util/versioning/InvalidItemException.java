package com.github.unknownstudio.unknowndomain.engineapi.util.versioning;

public class InvalidItemException extends Exception {
	public InvalidItemException(Class<?> clazz){
		super("invalid item: " + clazz);
	}
}
