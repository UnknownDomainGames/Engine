package nullengine.mod.init.handler;

import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationHandler;
import nullengine.mod.init.ModInitializer;

public class InstanceHandler implements ModInitializationHandler {
    @Override
    public void handle(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().register(mod.getInstance());
    }
}
