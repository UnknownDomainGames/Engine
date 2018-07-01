package com.github.unknownstudio.unknowndomain.engine.registry;

import com.github.unknownstudio.unknowndomain.engineapi.event.Event;
import com.github.unknownstudio.unknowndomain.engineapi.registry.ResourcesManager;
import com.github.unknownstudio.unknowndomain.engineapi.registry.Registry;
import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public class RegistryEvent<T extends RegistryEntry<T>> implements Event {

    private final Registry<T> registry;

    public RegistryEvent(Registry<T> registry){
        this.registry = registry;
    }
}
