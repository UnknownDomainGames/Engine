package unknowndomain.engine.mod.init.handler;

import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.init.ModInitializationHandler;
import unknowndomain.engine.mod.init.ModInitializer;

public class InstanceHandler implements ModInitializationHandler {
    @Override
    public void handle(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().register(mod.getInstance());
        initializer.getEngine().getEventBus().register(mod.getInstance());
    }
}
