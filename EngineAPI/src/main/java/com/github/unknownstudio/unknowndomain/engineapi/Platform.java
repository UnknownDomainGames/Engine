package com.github.unknownstudio.unknowndomain.engineapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Engine Platform
 */
public class Platform {
	
    private static final Logger LOGGER = LoggerFactory.getLogger("Engine");
    private static final Logger CLIENT_LOGGER = LoggerFactory.getLogger("Engine Client");
    private static final Logger SERVER_LOGGER = LoggerFactory.getLogger("Engine Server");

	public static Logger getLogger() {
		return LOGGER;
	}

	public static Logger getClientLogger() {
		return CLIENT_LOGGER;
	}

	public static Logger getServerLogger() {
		return SERVER_LOGGER;
	}
}
