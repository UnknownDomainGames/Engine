package com.github.unknownstudio.knowndomain.engine.registry;

import java.util.List;

public interface IRegistry<T> {
    T registerAll(T... objects);
    T register(T object);
    List<T> getRegistryObjects();
}
