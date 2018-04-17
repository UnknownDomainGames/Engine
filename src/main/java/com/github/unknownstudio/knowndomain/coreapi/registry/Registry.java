package com.github.unknownstudio.knowndomain.coreapi.registry;

public interface Registry<T extends RegistyEntry<?>> {
	
	T register(T obj);
	
    default void registerAll(T... objs) {
    	for(T obj:objs)
    		register(obj);
    }
}
