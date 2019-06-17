package unknowndomain.engine.mod.init.handler;

import unknowndomain.engine.event.mod.ModLifecycleEvent;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.init.ModInitializationHandler;
import unknowndomain.engine.mod.init.ModInitializer;

public class PreInitializationHandler implements ModInitializationHandler {
    @Override
    public void handle(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().post(new ModLifecycleEvent.PreInitialization(mod));
    }
}
