package nullengine.mod.init.handler;

import nullengine.event.mod.ModRegistrationEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationTask;
import nullengine.mod.init.ModInitializer;
import nullengine.registry.Registry;

import java.util.Map;

public class RegistrationTask implements ModInitializationTask {

    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        var registryManager = initializer.getEngine().getRegistryManager();
        mod.getEventBus().post(new ModRegistrationEvent.Construction(registryManager));
        mod.getEventBus().post(new ModRegistrationEvent.Pre(registryManager));
        for (Map.Entry<Class<?>, Registry<?>> registry : registryManager.getEntries()) {
            mod.getEventBus().post(new ModRegistrationEvent.Register<>(registry.getValue()));
        }
        mod.getEventBus().post(new ModRegistrationEvent.Post(registryManager));
    }
}
