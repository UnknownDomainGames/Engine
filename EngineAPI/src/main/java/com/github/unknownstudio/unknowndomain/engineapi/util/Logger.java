package com.github.unknownstudio.unknowndomain.engineapi.util;

import org.slf4j.impl.SimpleLogger;
import org.slf4j.impl.SimpleLoggerFactory;

public class Logger {
    private SimpleLoggerFactory loggerFactory;

    public Logger(){
        loggerFactory = new SimpleLoggerFactory();
    }

    public SimpleLogger getLogger(){
        return getLogger("UDEngine");
    }

    public SimpleLogger getLogger(String name){
        return (SimpleLogger) loggerFactory.getLogger(name);
    }
}
