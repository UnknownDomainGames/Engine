package nullengine.mod.init.handler;

import nullengine.event.mod.ModLifecycleEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationHandler;
import nullengine.mod.init.ModInitializer;

public class InitializationHandler implements ModInitializationHandler {
    @Override
    public void handle(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().post(new ModLifecycleEvent.Initialization(mod));
    }
}
